import request from './request';

// 获取报价列表
export const getQuotes = async () => {
  try {
    const response = await request.get('/pricing/quotes');
    console.log('getQuotes response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Get quotes error:', error);
    return [];
  }
};

// 获取报价详情
export const getQuoteDetail = async (quoteId: string) => {
  try {
    const response = await request.get(`/pricing/quote/${quoteId}`);
    console.log('getQuoteDetail response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Get quote detail error:', error);
    return null;
  }
};

// 根据位置和时间搜索报价
export const searchQuotes = async (params: {
  date: string;
  time: string;
  location: { lng: number; lat: number };
}) => {
  try {
    console.log('searchQuotes request params:', params);
    const response = await request.post('/pricing/search', {
      date: params.date,
      time: params.time,
      longitude: params.location.lng,
      latitude: params.location.lat
    });
    console.log('searchQuotes response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Search quotes error:', error);
    return { quotes: [] };
  }
};