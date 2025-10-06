/**
 * 订单状态枚举
 */
export enum OrderStatus {
  PENDING_PAYMENT = 1,  // 待支付
  PENDING_PICKUP = 2,   // 待取车
  PICKED_UP = 3,        // 已取车
  COMPLETED = 4,        // 已完成
  CANCELLED = 5         // 已取消
}

/**
 * 获取订单状态描述
 * @param status 订单状态码
 * @returns 状态描述
 */
export const getOrderStatusDescription = (status: number): string => {
  switch (status) {
    case OrderStatus.PENDING_PAYMENT:
      return '待支付';
    case OrderStatus.PENDING_PICKUP:
      return '待取车';
    case OrderStatus.PICKED_UP:
      return '已取车';
    case OrderStatus.COMPLETED:
      return '已完成';
    case OrderStatus.CANCELLED:
      return '已取消';
    default:
      return '未知状态';
  }
};

/**
 * 判断订单是否可以取消
 * @param status 订单状态
 * @returns 是否可以取消
 */
export const canCancelOrder = (status: number): boolean => {
  return status === OrderStatus.PENDING_PAYMENT || status === OrderStatus.PENDING_PICKUP;
};