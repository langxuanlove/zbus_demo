package com.util;

import java.util.logging.Logger;


public class LogUtil {
	/** 
     * 获取日志对象
     * 使用jdk中的logging更好,减少额外包的依赖
     * 如果有使用此类打印日志的,此处可以更换的变换日志对象 
     * @param clazz 
     * @return 
     */  
    public static Logger getLogger(Class<?> clazz) {  
        Logger logger = Logger.getLogger(clazz.getName());  
        return logger;  
    }  
}
