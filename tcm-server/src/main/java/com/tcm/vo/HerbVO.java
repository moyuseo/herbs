package com.tcm.vo;

public class HerbVO {
    private Long id;
    private String name;
    private String alias;
    private String pinyin;
    private Long categoryId;
    private String categoryName;
    private String medicinalPart;
    private String natureFlavor;
    private String meridianTropism;
    private String efficacy;

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
}
