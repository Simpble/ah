package com.shiep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sa_admin_category")
public class AdminCategory implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;
    private String name;
}
