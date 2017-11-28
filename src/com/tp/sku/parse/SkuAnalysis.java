package com.tp.sku.parse;

import com.tp.sku.annotations.SKUCount;
import com.tp.sku.annotations.SKUId;
import com.tp.sku.annotations.SKUKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * analysis
 *
 * @author
 * @create 2017-11-28 9:59
 **/
public class SkuAnalysis {

    /**
     * 倒排索引 key 和 index 文档
     */
    HashMap<String,List<String>>  keyIndex = new HashMap<>();

    /**
     * key 组 将所有生产的索引找到组
     *
     * 例子： 红 ： color
     *       L :  size
     */
    Map<String,String> keyGroup = new HashMap<>();

    /**
     * SKU 文档数量
     */
    Map<String,Integer> docCount = new HashMap<>();

    /**
     * id key 索引
     */
    HashMap<String,List<String>>  idKeys = new HashMap<>();



    public static <T> SkuAnalysis analysis(List<T> skuEntity) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        SkuAnalysis skuAnalysis = new SkuAnalysis();
        for (T entity: skuEntity){
            skuAnalysis.parse(entity);
        }
        return  skuAnalysis;
    }

    public static <T> SkuAnalysis analysis(T skuEntity) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
         SkuAnalysis skuAnalysis = new SkuAnalysis();
         skuAnalysis.parse(skuEntity);
        return  skuAnalysis;
    }

    /**
     * 解析
     * @param entity
     */
    private  <T> void parse(T entity) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
          Class cl = entity.getClass();
          String id  = getId(entity);
          int count = getCount(entity);

          if (id == null || "".equals(id)){// 垃圾数据
              return;
          }

          docCount.put(id,count);
          queryKey(entity,id,count);
    }

    /**
     * 查询key
     * @param entity
     * @param id
     * @param <T>
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private <T> void queryKey(T entity,String id,int count) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class cl = entity.getClass();
        for (Field field : cl.getFields()){

            Annotation[] annotations = PropertyUtils.getFieldAnnotations(cl, field.getName());
            if (annotations !=null){
                for (Annotation annotation : annotations){

                    if (annotation.annotationType().equals(SKUKey.class)){
                        String key = (String) PropertyUtils.invokeGetter(entity,field.getName());
                        List<String> values = keyIndex.get(key);
                        if (values == null){
                            values = new ArrayList<>();
                        }
                        if (count !=0) {//0的不需要放在文档中，优化过滤规则
                            values.add(id);
                        }
                        keyIndex.put(key,values);

                       Method name = annotation.getClass().getDeclaredMethod("name");
                       String group = (String) name.invoke(annotation);
                        if (group ==null || "".equals(group)){
                            group = field.getName(); //名称作为组
                        }
                        //根据key 找到对应的组
                        keyGroup.put(key,group);

                        //id key
                        values = idKeys.get(id);
                        if (values == null){
                            values = new ArrayList<>();
                        }
                        values.add(key);
                        idKeys.put(id,values);
                    }
                }
            }
        }

    }

    /**
     * 获取SKU ID
     * @param entity
     * @param <T>
     * @return
     */
    private <T> String getId(T entity){
        Class cl = entity.getClass();
        String id  = null;
        for (Field field : cl.getFields()){

            Annotation[] annotations = PropertyUtils.getFieldAnnotations(cl, field.getName());
            if (annotations !=null){
                for (Annotation annotation : annotations){

                    if (annotation.annotationType().equals(SKUId.class)){
                        id = (String) PropertyUtils.invokeGetter(entity,field.getName());
                        return  id;
                    }
                }
            }
        }

        return  null;
    }

    /**
     * 获取SKU count数量
     * @param entity
     * @param <T>
     * @return
     */
    private <T> int getCount(T entity){
        Class cl = entity.getClass();
        int count = 0;
        for (Field field : cl.getFields()){

            Annotation[] annotations = PropertyUtils.getFieldAnnotations(cl, field.getName());
            if (annotations !=null){
                for (Annotation annotation : annotations){

                    if (annotation.annotationType().equals(SKUCount.class)){
                        count = (int) PropertyUtils.invokeGetter(entity,field.getName());
                        return  count;
                    }
                }
            }
        }

        return  count;
    }


    /**
     * 根据关键字过滤 SKU
     * @param keys
     * @return
     */
    public List<String> querySkuId(List<String> keys){
         List<String> result = null;
          for (String key : keys) {
              List indexs = keyIndex.get(key);
              if (indexs == null){ //没有找到代表无结果
                  return  null;
              }
              if (result == null){
                  result = indexs;
              }else {
                  result.retainAll(indexs);
              }
          }

          return  result;
    }

    /**
     * 根据关键字过滤 SKU
     * @param keys
     * @return
     */
    public List<String> querySkuKeys(List<String> keys){
        List<String> result = querySkuId(keys);
        result = getKeys(result);
        return  result;
    }


    /**
     * 根据id获取所有的id
     * @param ids
     * @return
     */
    public List<String> getKeys(List<String> ids){
        List<String> result = new ArrayList<>();
        for (String  id: ids){
            List<String> values = idKeys.get(id);
            result.addAll(values);
        }
        return  result;
    }

    public HashMap<String, List<String>> getKeyIndex() {
        return keyIndex;
    }

    public Map<String, String> getKeyGroup() {
        return keyGroup;
    }

    public Map<String, Integer> getDocCount() {
        return docCount;
    }
}
