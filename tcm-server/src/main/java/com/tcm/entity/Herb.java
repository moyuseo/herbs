package com.tcm.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("herb")
public class Herb {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String alias;

    private String pinyin;

    private Long categoryId;

    private String medicinalPart;

    private String natureFlavor;

    private String meridianTropism;

    private String efficacy;

    private String indication;

    private String description;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getMedicinalPart() {
        return medicinalPart;
    }

    public void setMedicinalPart(String medicinalPart) {
        this.medicinalPart = medicinalPart;
    }

    public String getNatureFlavor() {
        return natureFlavor;
    }

    public void setNatureFlavor(String natureFlavor) {
        this.natureFlavor = natureFlavor;
    }

    public String getMeridianTropism() {
        return meridianTropism;
    }

    public void setMeridianTropism(String meridianTropism) {
        this.meridianTropism = meridianTropism;
    }

    public String getEfficacy() {
        return efficacy;
    }

    public void setEfficacy(String efficacy) {
        this.efficacy = efficacy;
    }

    public String getIndication() {
        return indication;
    }

    public void setIndication(String indication) {
        this.indication = indication;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
