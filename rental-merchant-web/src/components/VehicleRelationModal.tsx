import React, { useState, useEffect } from 'react';
import { Modal, Table, Button, message, Space, Tag, Spin } from 'antd';
import type { TableColumnsType } from 'antd';
import { useVehicleRelations } from '../hooks/useVehicleRelations';

interface VehicleInfo {
  id: number;
  licensePlate: string;
  vehicleStatus: number;
  related: boolean;
}

interface VehicleRelationModalProps {
  visible: boolean;
  productId: number | null;
  onCancel: () => void;
  onOk: () => void;
}

const VehicleRelationModal: React.FC<VehicleRelationModalProps> = ({ 
  visible, 
  productId, 
  onCancel, 
  onOk 
}) => {
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const { vehicles, loading, relatedVehicles, fetchAvailableVehicles, relateVehicles, unrelateVehicles } = useVehicleRelations(productId);

  // 当模态框打开且productId存在时，获取车辆数据
  useEffect(() => {
    if (visible && productId) {
      fetchAvailableVehicles();
    }
  }, [visible, productId]);

  // 当relatedVehicles更新时，同步选中状态
  useEffect(() => {
    setSelectedRowKeys(relatedVehicles);
  }, [relatedVehicles]);

  // 处理车辆关联/取消关联
  const handleRelationChange = async () => {
    if (!productId) return;
    
    try {
      // 获取需要关联的车辆（新选中但未关联的）
      const toRelate = selectedRowKeys.filter(key => !relatedVehicles.includes(Number(key)));
      // 获取需要取消关联的车辆（已关联但未选中的）
      const toUnrelate = relatedVehicles.filter(id => !selectedRowKeys.includes(id));
      
      let success = true;
      
      // 关联新选中的车辆
      if (toRelate.length > 0) {
        const result = await relateVehicles(toRelate.map(Number));
        if (!result) success = false;
      }
      
      // 取消未选中的车辆关联
      if (toUnrelate.length > 0) {
        const result = await unrelateVehicles(toUnrelate);
        if (!result) success = false;
      }
      
      if (success) {
        message.success('操作成功');
        onOk();
      }
    } catch (error) {
      message.error('操作失败');
    }
  };

  // 表格列定义
  const columns: TableColumnsType<VehicleInfo> = [
    {
      title: '车牌号',
      dataIndex: 'licensePlate',
      key: 'licensePlate',
    },
    {
      title: '状态',
      dataIndex: 'vehicleStatus',
      key: 'vehicleStatus',
      render: (value) => {
        const statusMap: Record<number, { text: string; color: string }> = {
          1: { text: '空闲', color: 'green' },
          2: { text: '租出', color: 'blue' },
          3: { text: '维修', color: 'orange' },
          4: { text: '报废', color: 'red' },
        };
        const status = statusMap[value] || { text: '未知', color: 'default' };
        return <Tag color={status.color}>{status.text}</Tag>;
      },
    },
    {
      title: '关联状态',
      dataIndex: 'related',
      key: 'related',
      render: (value) => (
        <Tag color={value ? 'green' : 'default'}>
          {value ? '已关联' : '未关联'}
        </Tag>
      ),
    },
  ];

  // 表格行选择配置
  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedKeys: React.Key[]) => {
      setSelectedRowKeys(selectedKeys);
    },
  };

  return (
    <Modal
      title="车辆关联管理"
      open={visible}
      onCancel={onCancel}
      onOk={handleRelationChange}
      width={800}
      footer={[
        <Button key="back" onClick={onCancel}>
          取消
        </Button>,
        <Button key="submit" type="primary" onClick={handleRelationChange}>
          保存
        </Button>,
      ]}
    >
      <Spin spinning={loading}>
        <Table
          rowKey="id"
          columns={columns}
          dataSource={vehicles}
          rowSelection={rowSelection}
          pagination={false}
          scroll={{ y: 400 }}
        />
      </Spin>
    </Modal>
  );
};

export default VehicleRelationModal;