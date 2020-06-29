/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyhadoopmonitor.controller.xmlrpc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Queue;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class ZooKeeperConfigController {
    private Logger log=Logger.getLogger(ZooKeeperConfigController.class);
    public int createConfig(String dir,String filename){
        File file=new File(dir,filename);
        if (file.isFile()&&file.exists()){
            file.delete();
        }
        try{
            file.createNewFile();
            return 1;
        }catch(IOException e){
            log.error(e.toString());
            return 0;
        }
    }

    public int writeConfig(String dir,String filename,List<String> proplist){
        try {
            OutputStream os = new FileOutputStream(new File(dir,filename));
            for(String s:proplist){
                log.info(s);
                os.write(s.getBytes());
                os.write("\n".getBytes());
            }
            os.flush();
            //properties.store(out, null);    
        } catch (IOException e) {
            log.error(e.toString());
            return 0;
        }
        return 1;
    }
}
