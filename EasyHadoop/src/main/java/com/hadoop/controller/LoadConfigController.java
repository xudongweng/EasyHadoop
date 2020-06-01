/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class LoadConfigController {
    private final Logger log=Logger.getLogger(LoadConfigController.class);
    private final Properties prop = new Properties();
    public int loadFile(){
        File file = new File("hadoop.properties");
        if(!file.exists()){
            log.error("File "+file.getAbsolutePath()+"hadoop.properties is not exist.");
            return 0;
        }
        //加载配置
        try{
            prop.load(new FileInputStream("hadoop.properties"));
        }catch(IOException e){
            log.error(e.toString());
            return 0;
        }
        return 1;
    }
    

}
