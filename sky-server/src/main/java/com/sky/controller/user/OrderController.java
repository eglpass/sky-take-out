package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController" )
@Slf4j
@RequestMapping("/user/order" )
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public Result<OrderSubmitVO> submitOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
       log.info("用户下单：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/payment")
    public Result<OrderPaymentVO> paySuccess(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        log.info("支付成功：{}",ordersPaymentDTO);
     //   orderService.paySuccess(outTradeNo);
        //修改订单的状态
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber(), Orders.PAID);
        return Result.success(new OrderPaymentVO());
    }
    @GetMapping("/historyOrders")

    public Result<PageResult> page(int page, int pageSize, Integer status) {
        PageResult pageResult = orderService.pageQuery4User(page, pageSize, status);
        return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> detail(@PathVariable("id") Long id) {
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }

    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable("id") Long id) {
        orderService.cancel(id);
        return Result.success();
    }
    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable("id") Long id) {
        orderService.repetition(id);
        return Result.success();
    }

}
