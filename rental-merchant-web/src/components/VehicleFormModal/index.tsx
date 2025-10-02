import React, { useState, useEffect } from 'react'
import { Modal, Form, Input, DatePicker, InputNumber, Select, message } from 'antd'
import { Vehicle } from '../../hooks/useVehicles'
import { useStores } from '../../hooks/useStores'
import { useCarModels } from '../../hooks/useCarModels'
import dayjs from 'dayjs'

interface VehicleFormModalProps {
  visible: boolean
  onCancel: () => void
  onSubmit: (values: any) => Promise<void>
  initialValues?: Vehicle
  title: string
}

const VehicleFormModal: React.FC<VehicleFormModalProps> = ({
  visible,
  onCancel,
  onSubmit,
  initialValues,
  title
}) => {
  const [form] = Form.useForm()
  const { stores } = useStores()
  const { carModels, loading: carModelsLoading } = useCarModels()

  const storeOptions = Array.isArray(stores) ? stores.map(store => ({
    label: store.storeName || store.name || '',
    value: store.id
  })) : []

  const carModelOptions = Array.isArray(carModels) ? carModels.map(carModel => ({
    label: `${carModel.brand}${carModel.series} - ${carModel.model}`,
    value: carModel.id
  })) : []

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      
      // 处理日期格式
      if (values.registerDate) {
        values.registerDate = values.registerDate.format('YYYY-MM-DD')
      }
      
      await onSubmit(values)
      form.resetFields()
    } catch (error) {
      console.error('表单验证失败:', error)
    }
  }

  const handleCancel = () => {
    form.resetFields()
    onCancel()
  }

  // 当初始值变化时，设置表单值
  useEffect(() => {
    if (visible && initialValues) {
      const formValues = {
        ...initialValues,
        registerDate: initialValues.registerDate 
          ? dayjs(initialValues.registerDate) 
          : undefined,
      }
      form.setFieldsValue(formValues)
    } else if (visible) {
      form.resetFields()
    }
  }, [visible, initialValues, form])

  return (
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
          licenseType: 1,
          usageNature: 1,
          mileage: 0,
        }}
      >
        <Form.Item
          label="归属门店"
          name="storeId"
          rules={[{ required: true, message: '请选择归属门店' }]}
        >
          <Select placeholder="请选择门店" options={storeOptions} />
        </Form.Item>

        <Form.Item
          label="车牌号"
          name="licensePlate"
          rules={[
            { required: true, message: '请输入车牌号' },
            { 
              pattern: /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳]$/, 
              message: '车牌号格式不正确' 
            }
          ]}
        >
          <Input placeholder="请输入车牌号，如：京A12345" />
        </Form.Item>

        <Form.Item
          label="车型ID"
          name="carModelId"
          rules={[{ required: true, message: '请选择车型' }]}
        >
          <Select 
            placeholder="请选择车型" 
            loading={carModelsLoading}
            options={carModelOptions}
            showSearch
            filterOption={(input, option) =>
              (option?.label?.toLowerCase().includes(input.toLowerCase())) || false
            }
          />
        </Form.Item>

        <Form.Item
          label="牌照类型"
          name="licenseType"
          rules={[{ required: true, message: '请选择牌照类型' }]}
        >
          <Select placeholder="请选择牌照类型">
            <Select.Option value={1}>普通</Select.Option>
            <Select.Option value={2}>京牌</Select.Option>
            <Select.Option value={3}>沪牌</Select.Option>
            <Select.Option value={4}>深牌</Select.Option>
            <Select.Option value={5}>粤A牌</Select.Option>
            <Select.Option value={6}>杭州牌</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          label="注册日期"
          name="registerDate"
          rules={[{ required: true, message: '请选择注册日期' }]}
        >
          <DatePicker 
            placeholder="请选择注册日期" 
            style={{ width: '100%' }}
            disabledDate={(current) => current && current > dayjs()}
          />
        </Form.Item>

        <Form.Item
          label="车架号"
          name="vin"
          rules={[
            { required: true, message: '请输入车架号' },
            { 
              pattern: /^[A-HJ-NPR-Z\d]{17}$/, 
              message: '车架号格式不正确，应为17位字符' 
            }
          ]}
        >
          <Input 
            placeholder="请输入17位车架号" 
            maxLength={17}
          />
        </Form.Item>

        <Form.Item
          label="发动机号"
          name="engineNo"
          rules={[
            { required: true, message: '请输入发动机号' },
            { max: 50, message: '发动机号不能超过50个字符' }
          ]}
        >
          <Input placeholder="请输入发动机号" />
        </Form.Item>

        <Form.Item
          label="使用性质"
          name="usageNature"
          rules={[{ required: true, message: '请选择使用性质' }]}
        >
          <Select placeholder="请选择使用性质">
            <Select.Option value={1}>营运</Select.Option>
            <Select.Option value={2}>非营运</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          label="总里程(公里)"
          name="mileage"
          rules={[{ type: 'number', min: 0, message: '总里程不能为负数' }]}
        >
          <InputNumber
            placeholder="请输入总里程"
            style={{ width: '100%' }}
            min={0}
            addonAfter="公里"
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default VehicleFormModal