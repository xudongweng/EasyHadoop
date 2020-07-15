/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyhadoopmonitor.controller.xmlrpc;

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
        String hostname=runtime.exec("hostname");
        String zoonum="0";
        String datadir="";
        try {
            OutputStream os = new FileOutputStream(new File(dir,filename));
            for(String s:proplist){
                log.info(s);
                os.write(s.getBytes());
                os.write("\n".getBytes());
                if(s.contains("dataDir=")){
                    datadir=s.substring(8);
                    log.info("Create directory "+s+" "+runtime.exec("mkdir -p "+datadir));
                }else if(s.contains(hostname)){
                    zoonum=s.substring(7,8);
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
