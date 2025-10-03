import React, { useState, useEffect } from 'react';
import { Modal, Button, message, Spin, InputNumber, Card, Row, Col, Typography, Table } from 'antd';
import { useSpecialPricingCalendar } from '../hooks/useSpecialPricingCalendar';
import dayjs, { Dayjs } from 'dayjs';
import type { ColumnsType } from 'antd/es/table';

const { Title, Text } = Typography;

interface SpecialPricingCalendarModalProps {
  visible: boolean;
  productId: number | null;
  onCancel: () => void;
  onOk: () => void;
}

// 定义表格数据类型
interface CalendarDay {
  key: string;
  date: string;
  dayOfWeek: string;
  price: number | null;
  dateString: string;
}

const SpecialPricingCalendarModal: React.FC<SpecialPricingCalendarModalProps> = ({ 
  visible, 
  productId, 
  onCancel, 
  onOk 
}) => {
  const { pricings, loading, fetchPricingsForCalendar, updatePricing } = useSpecialPricingCalendar(productId);
  const [currentMonth, setCurrentMonth] = useState<Dayjs>(dayjs());
  const [editingDate, setEditingDate] = useState<string | null>(null);
  const [editingPrice, setEditingPrice] = useState<number | null>(null);
  const [weeklyData, setWeeklyData] = useState<CalendarDay[][]>([]);

  // 当模态框打开且productId存在时，获取定价数据
  useEffect(() => {
    if (visible && productId) {
      fetchPricingsForCalendar();
    }
  }, [visible, productId]);

  // 当定价数据或月份变化时，重新生成周数据
  useEffect(() => {
    generateWeeklyData();
  }, [pricings, currentMonth]);

  // 生成按周分组的数据
  const generateWeeklyData = () => {
    const startOfMonth = currentMonth.startOf('month');
    const endOfMonth = currentMonth.endOf('month');
    
    // 获取该月的第一天和最后一天所在的周
    const startDate = startOfMonth.startOf('week');
    const endDate = endOfMonth.endOf('week');
    
    // 生成所有日期
    const days: Dayjs[] = [];
    let currentDate = startDate;
    
    while (currentDate.isBefore(endDate) || currentDate.isSame(endDate, 'day')) {
      days.push(currentDate);
      currentDate = currentDate.add(1, 'day');
    }
    
    // 按周分组
    const weeks: CalendarDay[][] = [];
    let currentWeek: CalendarDay[] = [];
    
    days.forEach((day, index) => {
      const dateStr = day.format('YYYY-MM-DD');
      const isCurrentMonth = day.month() === currentMonth.month();
      const pricing = pricings[dateStr];
      
      currentWeek.push({
        key: dateStr,
        date: day.date().toString(),
        dayOfWeek: day.format('ddd'),
        price: pricing ? pricing.price : null,
        dateString: dateStr
      });
      
      // 每7天为一周
      if ((index + 1) % 7 === 0) {
        weeks.push([...currentWeek]);
        currentWeek = [];
      }
    });
    
    // 添加最后一周（如果有的话）
    if (currentWeek.length > 0) {
      weeks.push([...currentWeek]);
    }
    
    setWeeklyData(weeks);
  };

  // 处理日期点击
  const handleDateClick = (dateString: string) => {
    setEditingDate(dateString);
    const pricing = pricings[dateString];
    setEditingPrice(pricing ? pricing.price / 100 : null); // 转换为元
  };

  // 保存价格
  const handleSavePrice = async () => {
    if (!editingDate || !productId) return;
    
    const success = await updatePricing(editingDate, editingPrice || 0);
    if (success) {
      message.success('保存成功');
      setEditingDate(null);
      setEditingPrice(null);
    }
  };

  // 切换月份
  const goToPreviousMonth = () => {
    setCurrentMonth(currentMonth.subtract(1, 'month'));
  };

  const goToNextMonth = () => {
    setCurrentMonth(currentMonth.add(1, 'month'));
  };

  const goToToday = () => {
    setCurrentMonth(dayjs());
  };

  // 渲染日历头部
  const renderCalendarHeader = () => (
    <div style={{ textAlign: 'center', marginBottom: 16 }}>
      <Button onClick={goToPreviousMonth}>上个月</Button>
      <Button onClick={goToToday} style={{ margin: '0 8px' }}>
        今天
      </Button>
      <Button onClick={goToNextMonth}>下个月</Button>
      <Title level={4} style={{ marginTop: 16 }}>
        {currentMonth.format('YYYY年MM月')}
      </Title>
    </div>
  );

  // 表格列定义
  const columns: ColumnsType<CalendarDay> = [
    {
      title: '周/日',
      dataIndex: 'dayOfWeek',
      width: 80,
      align: 'center',
    },
    {
      title: '日期',
      dataIndex: 'date',
      width: 80,
      align: 'center',
      render: (_, record) => {
        const day = dayjs(record.dateString);
        const isCurrentMonth = day.month() === currentMonth.month();
        const isToday = day.isSame(dayjs(), 'day');
        
        return (
          <div 
            style={{
              color: isCurrentMonth ? (isToday ? '#1890ff' : 'inherit') : '#ccc',
              fontWeight: isToday ? 'bold' : 'normal',
              opacity: isCurrentMonth ? 1 : 0.5
            }}
          >
            {record.date}
          </div>
        );
      }
    },
    {
      title: '价格',
      dataIndex: 'price',
      align: 'center',
      render: (value, record) => {
        const day = dayjs(record.dateString);
        const isCurrentMonth = day.month() === currentMonth.month();
        
        if (!isCurrentMonth) {
          return null;
        }
        
        return value ? (
          <div 
            style={{ 
              backgroundColor: '#1890ff', 
              color: '#fff', 
              padding: '2px 8px', 
              borderRadius: 4,
              cursor: 'pointer'
            }}
            onClick={() => handleDateClick(record.dateString)}
          >
            ¥{(value / 100).toFixed(2)}
          </div>
        ) : (
          <Button 
            type="link" 
            size="small" 
            onClick={() => handleDateClick(record.dateString)}
          >
            设置价格
          </Button>
        );
      }
    }
  ];

  // 渲染周表格
  const renderWeeklyTables = () => {
    return weeklyData.map((week, weekIndex) => (
      <div key={weekIndex} style={{ marginBottom: 16 }}>
        <Table
          columns={columns}
          dataSource={week}
          pagination={false}
          showHeader={weekIndex === 0}
          size="small"
          onRow={(record) => ({
            onClick: () => {
              const day = dayjs(record.dateString);
              const isCurrentMonth = day.month() === currentMonth.month();
              if (isCurrentMonth) {
                handleDateClick(record.dateString);
              }
            }
          })}
          rowClassName={(record) => {
            const day = dayjs(record.dateString);
            const isCurrentMonth = day.month() === currentMonth.month();
            return isCurrentMonth ? '' : 'disabled-row';
          }}
        />
      </div>
    ));
  };

  // 渲染价格编辑面板
  const renderPriceEditor = () => {
    if (!editingDate) return null;
    
    const day = dayjs(editingDate);
    const pricing = pricings[editingDate];
    
    return (
      <Card 
        title={`设置价格 - ${day.format('YYYY年MM月DD日')} (${day.format('dddd')})`} 
        size="small"
        style={{ marginTop: 16 }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <Text>价格:</Text>
          <InputNumber
            value={editingPrice}
            onChange={setEditingPrice}
            placeholder="请输入价格"
            min={0}
            step={0.01}
            formatter={value => `¥ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
            parser={value => value!.replace(/¥\s?|(,*)/g, '') as any}
            style={{ width: 120 }}
          />
          <Button onClick={handleSavePrice} type="primary">
            保存
          </Button>
          <Button onClick={() => setEditingDate(null)}>
            取消
          </Button>
        </div>
        {pricing && (
          <Text type="secondary" style={{ display: 'block', marginTop: 8 }}>
            当前已有价格: ¥{(pricing.price / 100).toFixed(2)}
          </Text>
        )}
      </Card>
    );
  };

  return (
    <Modal
      title="特殊定价日历"
      open={visible}
      onCancel={onCancel}
      onOk={onOk}
      width={800}
      footer={[
        <Button key="back" onClick={onCancel}>
          关闭
        </Button>,
      ]}
    >
      <Spin spinning={loading}>
        {renderCalendarHeader()}
        <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
          {renderWeeklyTables()}
        </div>
        {renderPriceEditor()}
      </Spin>
    </Modal>
  );
};

export default SpecialPricingCalendarModal;