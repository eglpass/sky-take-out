package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO disDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(disDTO,dish);
        dish.setStatus(0);
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        dish.setCreateUser(BaseContext.getCurrentId());
        dish.setUpdateUser(BaseContext.getCurrentId());

        dishMapper.insert(dish);
        Long dishId = dish.getId();
        List<DishFlavor> flavors = disDTO.getFlavors();
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dishId);
        });
        if(flavors != null && flavors.size() > 0){
            dishFlavorMapper.insertBatch(flavors);


        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
       PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        Long total = page.getTotal();
        List<DishVO> records = page.getResult();

        return new PageResult(total, records);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否在起售中
        //判断是否关联套餐
        //菜品口味一起删除
        for(Long id : ids){
            Dish byId = dishMapper.getById(id);
            if(byId.getStatus() == 1){
                //起售中
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        List<Long> setmealIdsByDishIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIdsByDishIds != null && setmealIdsByDishIds.size() > 0){
            //关联套餐
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        for(Long id : ids){
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }


    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish byId = dishMapper.getById(id);
        List<DishFlavor> byDishId = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(byId,dishVO);
        dishVO.setFlavors(byDishId);
        return  dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dish.setUpdateTime(LocalDateTime.now());
        dish.setUpdateUser(BaseContext.getCurrentId());
        dishMapper.update(dish);
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        Long dishId = dishDTO.getId();
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dishId);
        });
        if(flavors != null && flavors.size() > 0){
            dishFlavorMapper.insertBatch(flavors);


        }

    }
}
