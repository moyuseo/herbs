package com.tcm.module.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("herb_spec")
public class HerbSpec {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long herbId;
    private String specName;
    private Long parentId;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
