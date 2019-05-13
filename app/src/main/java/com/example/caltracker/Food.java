package com.example.caltracker;

public class Food {
    private Integer fid;
    private String name;
    private String category;
    private int calamount;
    private String servingunit;
    private String servingamount;
    private int fat;

    public Food(Integer fid, String name, String category, int calamount, String servingunit, String servingamount, int fat) {
        this.fid = fid;
        this.name = name;
        this.category = category;
        this.calamount = calamount;
        this.servingunit = servingunit;
        this.servingamount = servingamount;
        this.fat = fat;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCalamount() {
        return calamount;
    }

    public void setCalamount(int calamount) {
        this.calamount = calamount;
    }

    public String getServingunit() {
        return servingunit;
    }

    public void setServingunit(String servingunit) {
        this.servingunit = servingunit;
    }

    public String getServingamount() {
        return servingamount;
    }

    public void setServingamount(String servingamount) {
        this.servingamount = servingamount;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

}
