/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.controller;

import com.hadoop.helper.SSHLinuxHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class HadoopController {
    private final Logger log=Logger.getLogger(HadoopController.class);
    private final SSHLinuxHelper ssh=new SSHLinuxHelper();
    private final Properties prop = new Properties();
    private String jarfilename;
    private final String dst="/tmp";
    public int loadFile(String cfgfile){
        File file = new File(cfgfile);
        if(!file.exists()){
            log.error("File "+file.getAbsolutePath()+cfgfile+" is not exist.");
            return 0;
        }
        //加载配置
        try{
            prop.load(new FileInputStream(cfgfile));
        }catch(IOException e){
            log.error(e.toString());
            return 0;
        }
        return 1;
    }
    
    public List<String> getHadoop(){
        prop.clear();
        return null;
    }
}
