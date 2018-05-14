package com.ydg.context;

import com.ydg.config.Bean;
import com.ydg.config.Property;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yudungang
 */
public class ConfigManager {

    public static Map<String,Bean> getConfig(String path){
        Map<String,Bean> map = new HashMap<>();
        //1. 创建解析器
        SAXReader saxReader = new SAXReader();
        //2.加载配置文件
        InputStream is = ConfigManager.class.getResourceAsStream(path);
        Document document = null;
        try {
          document =  saxReader.read(is);
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException("请检查xml配置");
        }
        //3 定义xpath表达式取出所有bean元素
        String xpath = "//bean";
        //4 对bean元素进行遍历
        List<Element> list = document.selectNodes(xpath);
        if(list != null){
            for (Element beandFile: list){
                Bean bean = new Bean();
                //将class,name属性封装到bean对象中
                String name = beandFile.attributeValue("name");
                String className = beandFile.attributeValue("class");
                bean.setName(name);
                bean.setClassName(className);
                //获得bean元素下的所有property元素，并将其属性封装到property子元素中
                List<Element> children =  beandFile.elements("property");
                if(children != null){
                    for(Element child: children){
                        Property property = new Property();
                        String pName =child.attributeValue("name");
                        String pValue = child.attributeValue("value");
                        String pRef = child.attributeValue("ref");

                        property.setName(pName);
                        property.setRef(pRef);
                        property.setValue(pValue);
                        bean.getProperties().add(property);
                    }
                }
                //将bean对象封装到map中用于返回
                map.put(name,bean);
            }
        }
        //5 返回map
        return map;
    }

}
