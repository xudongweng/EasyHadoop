/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.controller;

import com.hadoop.model.InstallFiles;
import com.hadoop.model.LinuxHost;
import com.hadoop.zookeeper.ZooKeeper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class LoadConfigController {
    private final Logger log=Logger.getLogger(LoadConfigController.class);
    private final Properties prop = new Properties();
    
    //private List<LinuxHost> hostlist=null;
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
    
    public List<LinuxHost> getcfgHosts(){
        String[] hosts=prop.getProperty("hosts").split(",");
        List<LinuxHost> hostlist=new ArrayList<>();
        for (String host : hosts) {
            LinuxHost linuxhost=new LinuxHost();
            linuxhost.setIP(host);
            linuxhost.setUser(prop.getProperty("sshuser"));
            linuxhost.setPassword(prop.getProperty("sshpassword"));
            linuxhost.setPort(Integer.parseInt(prop.getProperty("port")));
            hostlist.add(linuxhost);
        }
        return hostlist;
    }
    
    public InstallFiles getInstallFiles(){
        InstallFiles files=new InstallFiles();
        files.setJDK(prop.getProperty("jdk"));
        files.setZooKeeper(prop.getProperty("zookeeper"));
        files.setHadoop(prop.getProperty("hadoop"));
        return files;
    }
    
    public ZooKeeper getZoo(){
        ZooKeeper zoo=new ZooKeeper();
        zoo.setClientPort(Integer.parseInt(prop.getProperty("clientPort")));
        zoo.setDataDir(prop.getProperty("dataDir"));
        zoo.setInitLimit(Integer.parseInt(prop.getProperty("initLimit")));
        zoo.setSyncLimit(Integer.parseInt(prop.getProperty("syncLimit")));
        zoo.setTickTime(Integer.parseInt(prop.getProperty("tickTime")));
        return zoo;
    }
}
