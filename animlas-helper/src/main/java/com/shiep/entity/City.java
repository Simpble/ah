package com.shiep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sa_city")
public class City implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    @TableField("sa_province_id")
    private Long provinceId;

    public City(String name,Long provinceId){
        this.name = name;
        this.provinceId = provinceId;
    }

    public City(Long id, String name, Long provinceId) {
        this.id = id;
        this.name = name;
        this.provinceId = provinceId;
    }
}
