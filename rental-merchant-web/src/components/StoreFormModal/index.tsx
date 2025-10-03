import React, { useState } from 'react'
import { Modal, Form, Input, TimePicker, InputNumber, Select, message, Button, Space } from 'antd'
import { Store } from '../../hooks/useStores'
import dayjs from 'dayjs'
// 添加导入 StoreLocationPicker 组件
import StoreLocationPicker from '../StoreLocationPicker'

interface StoreFormModalProps {
  visible: boolean
  onCancel: () => void
  onSubmit: (values: any) => Promise<void>
  initialValues?: Store
  title: string
}

const StoreFormModal: React.FC<StoreFormModalProps> = ({
  visible,
  onCancel,
  onSubmit,
  initialValues,
  title
}) => {
  const [form] = Form.useForm()
  // 添加状态管理地图选点模态框
  const [locationPickerVisible, setLocationPickerVisible] = useState(false)
  const [selectedLocation, setSelectedLocation] = useState<{ longitude: number; latitude: number } | null>(null)

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      
      // 处理时间格式
      if (values.businessStartTime) {
        // 修改时间格式为 HH:mm
        values.businessStartTime = values.businessStartTime.format('HH:mm')
      }
      if (values.businessEndTime) {
        // 修改时间格式为 HH:mm
        values.businessEndTime = values.businessEndTime.format('HH:mm')
      }
      
      // 添加经纬度信息
      if (selectedLocation) {
        values.longitude = selectedLocation.longitude
        values.latitude = selectedLocation.latitude
      } else if (initialValues?.longitude && initialValues?.latitude) {
        values.longitude = initialValues.longitude
        values.latitude = initialValues.latitude
      }
      
      await onSubmit(values)
      form.resetFields()
      setSelectedLocation(null)
    } catch (error) {
      console.error('表单验证失败:', error)
    }
  }

  const handleCancel = () => {
    form.resetFields()
    setSelectedLocation(null)
    onCancel()
  }

  // 处理地图选点确认
  const handleLocationSelect = (location: { longitude: number; latitude: number }) => {
    setSelectedLocation(location)
    setLocationPickerVisible(false)
  }

  // 当初始值变化时，设置表单值
  React.useEffect(() => {
    if (visible && initialValues) {
      const formValues = {
        ...initialValues,
        businessStartTime: initialValues.businessStartTime 
          ? dayjs(initialValues.businessStartTime, 'HH:mm') 
          : undefined,
        businessEndTime: initialValues.businessEndTime 
          ? dayjs(initialValues.businessEndTime, 'HH:mm') 
          : undefined,
      }
      form.setFieldsValue(formValues)
      
      // 设置初始位置
      if (initialValues.longitude && initialValues.latitude) {
        setSelectedLocation({
          longitude: initialValues.longitude,
          latitude: initialValues.latitude
        })
      }
    } else if (visible) {
      form.resetFields()
      setSelectedLocation(null)
    }
  }, [visible, initialValues, form])

  return (
    <>
      <Modal
        title={title}
        open={visible}
        onCancel={handleCancel}
        onOk={handleSubmit}
        okText="保存"
        cancelText="取消"
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            minAdvanceHours: 2,
            maxAdvanceDays: 30,
            serviceFee: 0,
          }}
        >
          <Form.Item
            label="门店名称"
            name="storeName"
            rules={[
              { required: true, message: '请输入门店名称' },
              { max: 100, message: '门店名称不能超过100个字符' }
            ]}
          >
            <Input placeholder="请输入门店名称" />
          </Form.Item>

          <Form.Item
            label="所在城市"
            name="city"
            rules={[
              { required: true, message: '请选择所在城市' },
              { max: 50, message: '城市名称不能超过50个字符' }
            ]}
          >
            <Select placeholder="请选择城市">
              <Select.Option value="北京">北京</Select.Option>
              <Select.Option value="上海">上海</Select.Option>
              <Select.Option value="广州">广州</Select.Option>
              <Select.Option value="深圳">深圳</Select.Option>
              <Select.Option value="杭州">杭州</Select.Option>
              <Select.Option value="南京">南京</Select.Option>
              <Select.Option value="成都">成都</Select.Option>
              <Select.Option value="重庆">重庆</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="详细地址"
            name="address"
            rules={[
              { required: true, message: '请输入详细地址' },
              { max: 200, message: '地址不能超过200个字符' }
            ]}
          >
            <Input.TextArea 
              placeholder="请输入详细地址" 
              rows={3} 
            />
          </Form.Item>

          {/* 修改经纬度选择方式 */}
          <Form.Item label="门店位置">
            <Space direction="vertical" style={{ width: '100%' }}>
              {selectedLocation ? (
                <div>
                  <p>经度: {selectedLocation.longitude.toFixed(6)}</p>
                  <p>纬度: {selectedLocation.latitude.toFixed(6)}</p>
                </div>
              ) : initialValues?.longitude && initialValues?.latitude ? (
                <div>
                  <p>经度: {initialValues.longitude.toFixed(6)}</p>
                  <p>纬度: {initialValues.latitude.toFixed(6)}</p>
                </div>
              ) : (
                <p>尚未选择位置</p>
              )}
              <Button type="primary" onClick={() => setLocationPickerVisible(true)}>
                {selectedLocation || (initialValues?.longitude && initialValues?.latitude) ? '重新选择位置' : '选择位置'}
              </Button>
            </Space>
          </Form.Item>

          <Form.Item label="营业时间">
            <Input.Group compact>
              <Form.Item
                name="businessStartTime"
                style={{ width: '50%' }}
                rules={[{ required: true, message: '请选择营业开始时间' }]}
              >
                {/* 修改时间格式，只显示小时和分钟 */}
                <TimePicker 
                  placeholder="开始时间" 
                  style={{ width: '100%' }}
                  format="HH:mm"
                  showNow={false}
                />
              </Form.Item>
              <Form.Item
                name="businessEndTime"
                style={{ width: '50%' }}
                rules={[{ required: true, message: '请选择营业结束时间' }]}
              >
                {/* 修改时间格式，只显示小时和分钟 */}
                <TimePicker 
                  placeholder="结束时间" 
                  style={{ width: '100%' }}
                  format="HH:mm"
                  showNow={false}
                />
              </Form.Item>
            </Input.Group>
          </Form.Item>

          <Form.Item
            label="最小提前预定时间(小时)"
            name="minAdvanceHours"
            rules={[
              { required: true, message: '请输入最小提前预定时间' },
              { type: 'number', min: 1, message: '最小提前预定时间不能少于1小时' }
            ]}
          >
            <InputNumber
              placeholder="小时"
              style={{ width: '100%' }}
              min={1}
            />
          </Form.Item>

          <Form.Item
            label="最大提前预定天数"
            name="maxAdvanceDays"
            rules={[
              { required: true, message: '请输入最大提前预定天数' },
              { type: 'number', min: 1, max: 365, message: '天数必须在1-365之间' }
            ]}
          >
            <InputNumber
              placeholder="天数"
              style={{ width: '100%' }}
              min={1}
              max={365}
            />
          </Form.Item>

          <Form.Item
            label="车行手续费(分)"
            name="serviceFee"
            rules={[
              { required: true, message: '请输入车行手续费' },
              { type: 'number', min: 0, message: '手续费不能为负数' }
            ]}
          >
            <InputNumber
              placeholder="分"
              style={{ width: '100%' }}
              min={0}
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* 添加地图选点模态框 */}
      <StoreLocationPicker
        visible={locationPickerVisible}
        onCancel={() => setLocationPickerVisible(false)}
        onConfirm={handleLocationSelect}
        initialLocation={selectedLocation || (initialValues?.longitude && initialValues?.latitude ? {
          longitude: initialValues.longitude,
          latitude: initialValues.latitude
        } : undefined)}
      />
    </>
  )
}

export default StoreFormModal