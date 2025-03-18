package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper  userMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double  turnover = orderMapper.sumByMap(map);
            log.info("查询日期:{},查询结果:{}",date,turnover);
            turnoverList.add(turnover == null ? 0.0 : turnover);

        }

        String join = StringUtils.join(dateList, ",");
        String join1 = StringUtils.join(turnoverList, ",");
        return   TurnoverReportVO.builder()
                .dateList(join)
                .turnoverList(join1)
                .build();


    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        log.info("查询用户数据：{} - {}", begin, end);
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> userList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);

            Integer count = userMapper.countByMap(map);
            userList.add(count==null?0:count);
            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser==null?0:newUser);
        }
        String join = StringUtils.join(dateList, ",");
        String join1 = StringUtils.join(userList, ",");
        String join2 = StringUtils.join(newUserList, ",");
        return UserReportVO.builder()
                .dateList(join)
                .totalUserList(join1)
                .newUserList(join2)
                .build();

    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        Double orderCompletionRate = 0.0;
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer count = orderMapper.countByMap(map);
            totalOrderCount += count==null?0:count;
            orderCountList.add(count==null?0:count);
            map.put("status",Orders.COMPLETED);
            Integer validCount = orderMapper.countByMap(map);
            validOrderCountList.add(validCount==null?0:validCount);
            validOrderCount += validCount==null?0:validCount;
           // log.info("查询日期:{},查询结果:{}",date,count);

        }
        orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();
        String datelist = StringUtils.join(dateList, ",");
        String orderCountList1 = StringUtils.join(orderCountList, ",");
        String validOrderCountList1 = StringUtils.join(validOrderCountList, ",");
        return OrderReportVO.builder()
                .orderCompletionRate(orderCompletionRate)
                .dateList(datelist)
                .orderCountList(orderCountList1)
                .validOrderCountList(validOrderCountList1)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .build();
    }
}
