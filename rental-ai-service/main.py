from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
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

class ChatResponse(BaseModel):
    role: str
    content: str

@app.get("/health")
async def health_check():
    return {"status": "ok"}

@app.post("/ai/chat", response_model=ChatResponse)  # 修改为 /chat 而不是 /ai/chat
async def chat(request: ChatRequest):
    # 调用Agent处理聊天消息
    messages = [{"role": msg.role, "content": msg.content} for msg in request.messages]
    result = await process_chat_message(request.tenant_id, messages)
    return ChatResponse(**result)

if __name__ == "__main__":
    port = int(os.getenv("PORT", 8280))
    uvicorn.run("main:app", host="0.0.0.0", port=port, reload=True)