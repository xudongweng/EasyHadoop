/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop;

import com.hadoop.controller.HadoopController;
import com.hadoop.controller.LoadConfigController;
import com.hadoop.model.InstallFiles;
import com.hadoop.model.LinuxHost;
import java.util.List;
import org.apache.log4j.Logger;



/**
 *
 * @author sheriff
 */
public class HadoopGenerate {
    public static void main(String[] args){
        Logger log=Logger.getLogger(HadoopGenerate.class);
        LoadConfigController lcc=new LoadConfigController();
        if(lcc.loadFile("config.properties")==0)return;
        
        InstallFiles files=lcc.getInstallFiles();

        List<LinuxHost> hostlist=lcc.getcfgHosts();
        
        HadoopController hc=new HadoopController();

        hc.configHadoop(hostlist, files.getHadoop());
        
        if(hc.loadFile("core.properties")==0)return;
        List<String> corelist=hc.getCfg();
        if(hc.loadFile("hdfs.properties")==0)return;
        List<String> hdfslist=hc.getCfg();
        if(hc.loadFile("mapred.properties")==0)return;
        List<String> mapredlist=hc.getCfg();
        if(hc.loadFile("yarn.properties")==0)return;
        List<String> yarnlist=hc.getCfg();
        //System.out.println(corelist);
        //System.out.println(hdfslist);
        //System.out.println(mapredlist);
        //System.out.println(yarnlist);
        
hc.uploadMonitor(hostlist, files.getMonitor());//上传ZooKeeperMonitor的jar
    }
}
