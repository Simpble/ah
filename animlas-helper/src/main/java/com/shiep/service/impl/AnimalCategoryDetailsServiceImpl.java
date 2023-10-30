package com.shiep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shiep.mapper.IAnimalCategoryDetailsMapper;
import com.shiep.entity.AnimalCategoryDetails;
import com.shiep.service.IAnimalCategoryDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AnimalCategoryDetailsServiceImpl implements IAnimalCategoryDetailsService {
    @Resource
    private IAnimalCategoryDetailsMapper animalCategoryDetailsMapper;

    @Override
    /*方法作用：根据动物分类id进行查询某一类动物的详细分类*/
    public List<AnimalCategoryDetails> queryByCategoryId(Integer id) {
        if (id == null) {
            return null;
        }
        QueryWrapper<AnimalCategoryDetails> qw = new QueryWrapper();
        qw.lambda().eq(AnimalCategoryDetails::getCategoryId, id);
        return this.animalCategoryDetailsMapper.selectList(qw);
    }
}
