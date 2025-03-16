package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    void paySuccess(String orderNumber, Integer payType);

    /**
     * 用户端订单分页查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    OrderVO getById(Long id);

    void cancel(Long id);

    void repetition(Long id);

    PageResult conditionPageQuery(OrdersPageQueryDTO orders);

    OrderStatisticsVO statistics();
    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;


    void admincancel(OrdersCancelDTO ordersCancelDTO);
    /**
     * 派送订单
     *
     * @param id
     */
    void delivery(Long id);

    void complete(Long id);
}
