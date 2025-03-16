package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCart.setCreateTime(LocalDateTime.now());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        //判断当前加入购物车的商品是否已经存在
        if(shoppingCartList != null && shoppingCartList.size() > 0){
            ShoppingCart cart = shoppingCartList.get(0);
            //加一即可
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        }
        //如果不存在那就添加到购物车
        //先判断是菜品还是套餐
        Long dishId = shoppingCartDTO.getDishId();
        if(dishId != null){
            Dish byId = dishMapper.getById(dishId);
            shoppingCart.setName(byId.getName());
            shoppingCart.setImage(byId.getImage());
            shoppingCart.setAmount(byId.getPrice());
            shoppingCart.setSetmealId(0L);

        }else{
            // tao can
            Setmeal byId = setmealMapper.getById(shoppingCartDTO.getSetmealId());
            shoppingCart.setName(byId.getName());
            shoppingCart.setImage(byId.getImage());
            shoppingCart.setAmount(byId.getPrice());
            shoppingCart.setDishId(0L);

        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());

        shoppingCartMapper.insert(shoppingCart);

    }

    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        return shoppingCartList;
    }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);

    }


}
