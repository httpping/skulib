package com.test.demo;

import com.tp.sku.annotations.SKUCount;
import com.tp.sku.annotations.SKUId;
import com.tp.sku.annotations.SKUKey;

import java.io.Serializable;

public class SkuEntity implements Serializable {

    @SKUKey(name = "color23")
    public String color ="红";

    @SKUKey
    public String size ="L";

    @SKUKey(name = "2g")
    public String neicun ="2G";

    @SKUId
    public String id = "123";

    @SKUCount
    public int count  =10;

    public String getNeicun() {
        return neicun;
    }

    public void setNeicun(String neicun) {
        this.neicun = neicun;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
