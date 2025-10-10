from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Optional, Any
import uvicorn
import os
from agent import process_chat_message
from dotenv import load_dotenv

load_dotenv(override = True)

app = FastAPI(title="Rental AI Service", version="1.0.0")

class ChatMessage(BaseModel):
    role: str
    content: str

class ChatRequest(BaseModel):
    messages: List[ChatMessage]
    tenant_id: int
    confirmed: Optional[bool] = False
    original_response: Optional[Any] = None

class ChatResponse(BaseModel):
    role: str
    content: str
    requires_confirmation: Optional[bool] = False
    action_type: Optional[str] = None
    action_description: Optional[str] = None

@app.get("/health")
async def health_check():
    return {"status": "ok"}

@app.post("/ai/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    # 调用Agent处理聊天消息
    messages = [{"role": msg.role, "content": msg.content} for msg in request.messages]
    result = await process_chat_message(request.tenant_id, messages, request.confirmed)
    return ChatResponse(**result)

if __name__ == "__main__":
    port = int(os.getenv("PORT", 8280))
    uvicorn.run("main:app", host="0.0.0.0", port=port, reload=True)