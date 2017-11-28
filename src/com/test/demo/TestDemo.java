package com.test.demo;

import com.tp.sku.parse.SkuAnalysis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @create 2017-11-28 10:37
 **/
public class TestDemo {

    public static void main(String[] args){
        System.out.println("解析");

        List<SkuEntity> skuEntities = new ArrayList<>();

        SkuEntity entity = new SkuEntity();

        skuEntities.add(entity);
        entity = new SkuEntity();
        entity.setId("31");
        entity.setColor("白色");
        entity.setCount(43);
        skuEntities.add(entity);


        entity = new SkuEntity();
        entity.setId("33");
        entity.setColor("白色");
        entity.setNeicun("8g");
        entity.setCount(9);
        skuEntities.add(entity);

        SkuAnalysis analysis = null;
        try {
            analysis = SkuAnalysis.analysis(skuEntities);
            System.out.println(analysis.getDocCount());
            System.out.println(analysis.getKeyGroup());
            System.out.println(analysis.getKeyIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String > select = new ArrayList<>();
        select.add("红");
//        select.add("8g");
        List result = analysis.querySkuId(select);
        System.out.println("result : " + result);

         result = analysis.querySkuKeys(select);
        System.out.println("result : " + result);

    }
}
