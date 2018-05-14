package com.ydg.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * @author yudungang
 */
public class BeanUtils {

    public static Method getWriteMethod(Object beanObj, String name){
        //使用内省实现
        Method method = null;

        try {
            //1:分析bean对象 --> BeanInfo
            BeanInfo beanInfo = Introspector.getBeanInfo(beanObj.getClass());
            //2: 根据BeanInfo 获取所有属性的描述器
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            //3. 遍历描述器
            if(pds != null){
                for (PropertyDescriptor pd: pds){
                    //判断当前属性是否是我们找的属性
                    String pName = pd.getName();
                    if(pName.equals(name)){
                       method = pd.getWriteMethod();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(method == null){
            throw new RuntimeException("请检查"+name+"属性的set方法是否创建");
        }
        return method;
    }

}
