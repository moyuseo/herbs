package com.tcm.module.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("herb")
public class Herb {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String pinyin;
    private Long categoryId;
    private String alias;
    private String originAreas;
    private String properties;
    private String meridian;
    private String efficacy;
    private String description;
    private String imageUrl;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
