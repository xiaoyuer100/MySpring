package com.ydg.context;

import com.ydg.config.Bean;
import com.ydg.config.Property;
import com.ydg.utils.BeanUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yudungang
 */
public class ClassPathXmlApplicationContext implements BeanFactory{

    //配置信息
    private Map<String ,Bean> config;
    //用一个map来做spring的容器，放置spring所管理的对象
    private Map<String , Object> context = new HashMap<>();


    //在ClassPathXmlApplicationContext一创建就初始化spring容器

    public Object getBean(String beanName){
        Object bean = context.get(beanName);
        return bean;
    }

    public ClassPathXmlApplicationContext(String path){
        //1 读取配置文件初始化的bean信息
        config = ConfigManager.getConfig(path);

        //2 遍历配置，初始化bean
        if(config != null){
            for (Map.Entry<String,Bean> en : config.entrySet()){
                //获取配置中的bean信息
                String beanName = en.getKey();
                Bean bean = en.getValue();

                Object exsitBean = context.get(beanName);
                //因为createBean方法中也会想context中放置bean，我们在初始化的时候先要查看已经存在bean
                //如果不存在再创建
                if(exsitBean == null){
                    //根据bean配置创建bean对象
                    Object beanObj = createBean(bean);
                    //3 将初始化的bean放入容器
                    context.put(beanName,beanObj);
                }
            }
        }
    }

    //根据bean配置创建bean对象
    private Object createBean(Bean bean) {
        //1 获取要创建的bean的class
        String className = bean.getClassName();
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("请检查bean的class配置" + className);
        }
        //将class对应的对象创建出来
        Object beanObj = null;
        try {
            beanObj = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("bean没有空参构造"+className);
        }

        //2.获得bean属性，将其注入
        if(bean.getProperties() != null){
            for (Property property : bean.getProperties()){
                //1:简单value注入
                //获取注入的属性名称
                String name = property.getName();
                //根据属性名称获得注入属性对应的set方法
                Method setMethod = BeanUtils.getWriteMethod(beanObj,name);
                //创建一个需要注入Bean的属性
                Object parm = null;
                if(property.getValue() != null){
                    String value = property.getValue();
                    parm = value;
                }
                //其他bean的注入
                if(property.getRef() != null){
                    //先从容器中查找当前要注入的bean是否已经创建并放入容器
                    Object  exsitBean = context.get(property.getRef());
                    if(exsitBean == null){
                        //如果容器不存在，则需创建
                        exsitBean = createBean(config.get(property.getRef()));
                        context.put(property.getRef(),exsitBean);
                    }
                    parm = exsitBean;
                }
                //调用set方法注入
                try {
                    Class<?> paramType = setMethod.getParameterTypes()[0];
                    parm =  convertToType(paramType,parm);
                    setMethod.invoke(beanObj,parm);
                }  catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("bean的属性"+parm+"没有对应的set方法，或者参数不正确"+className);
                }
            }
        }
        return beanObj;
    }

    private Object convertToType(Class<?> clazz, Object value){
        if(clazz.getName().equals("java.lang.Integer")){
            return Integer.valueOf(value.toString());
        }
        return value;
    }
}
