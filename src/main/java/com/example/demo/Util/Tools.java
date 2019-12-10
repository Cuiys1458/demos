package com.example.demo.Util;


import java.io.IOException;
import java.util.Properties;

/**
 *
 * 读取properties文件的工具类
 *
 * @author cuiyes 2019-3-19
 */
public class Tools {
    private static Properties p = new Properties();

    /**
     * 读取properties配置文件信息
     */
    static{
        try {
            p.load(Tools.class.getClassLoader().getResourceAsStream("ip.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 根据key得到value的值
     */
    public static String getValue(String key)
    {
        return p.getProperty(key);
    }
}