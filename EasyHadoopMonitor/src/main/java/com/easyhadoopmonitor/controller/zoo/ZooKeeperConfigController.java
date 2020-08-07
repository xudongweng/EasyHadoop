/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyhadoopmonitor.controller.zoo;

import com.easyhadoopmonitor.helper.LinuxRunTimeHelper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class ZooKeeperConfigController {
    private final Logger log=Logger.getLogger(ZooKeeperConfigController.class);
    private final LinuxRunTimeHelper runtime=new LinuxRunTimeHelper();

    public int writeConfig(String dir,String filename,List<String> proplist){
        String hostname=runtime.exec("hostname");
        //log.info("-----------------"+hostname+"---"+hostname.trim().length());
        String zoonum="0";
        String datadir="";
        try {
            OutputStream os = new FileOutputStream(new File(dir,filename));
            for(String prop:proplist){
                log.info("Write " +prop);
                os.write(prop.getBytes());
                os.write("\n".getBytes());
                if(prop.contains("dataDir=")){
                    datadir=prop.substring(8);
                    log.info("Remove directory "+prop+" "+runtime.exec("rm -rf "+datadir));
                    log.info("Create directory "+prop+" "+runtime.exec("mkdir -p "+datadir));
                }else if(prop.contains(hostname.trim())){
                    //log.info("-----------------"+prop+"---"+prop.indexOf("="));
                    zoonum=prop.substring(7,prop.indexOf("="));
                }
            }
            os.flush();
            this.writeMyid(zoonum, datadir);
        } catch (IOException e) {
            log.error(e.toString());
            return 0;
        }
        return 1;
    }

    private void writeMyid(String zoonum,String datadir){
        try{
            log.info("Write file myid" +datadir+":"+zoonum);
            File myidfile = new File(datadir+File.separator+"myid");
            if(!myidfile.isFile()){
                myidfile.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(myidfile),"utf-8"));
            bw.write(zoonum);
            bw.close();
        }catch(IOException e){
            log.error(e.toString());
        }
        
    }
}
