/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop;

import com.hadoop.controller.HadoopController;
import com.hadoop.controller.LoadConfigController;
import com.hadoop.controller.RPCClientController;
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
        hc.shutdownMonitor(hostlist);
        hc.uploadMonitor(hostlist, files.getMonitor());//上传HadoopMonitor的jar
        
        
        LinuxHost host=hostlist.get(0);
        RPCClientController rpcClient=new RPCClientController();
        if(rpcClient.invokeHadoopWrite("http://"+host.getIP()+":10080","/usr/local/hadoop/etc/hadoop","core-site.xml",corelist)==0){//调用名为hadoop的RPC
            log.error(host.getIP()+" : "+"The core-site.xml was configured which is failure.");
        }
        if(rpcClient.invokeHadoopWrite("http://"+host.getIP()+":10080","/usr/local/hadoop/etc/hadoop","hdfs-site.xml",hdfslist)==0){//调用名为hadoop的RPC
            log.error(host.getIP()+" : "+"The hdfs-site.xml was configured which is failure.");
        }
        if(rpcClient.invokeHadoopWrite("http://"+host.getIP()+":10080","/usr/local/hadoop/etc/hadoop","mapred-site.xml",mapredlist)==0){//调用名为hadoop的RPC
            log.error(host.getIP()+" : "+"The mapred-site.xml was configured which is failure.");
        }
        if(rpcClient.invokeHadoopWrite("http://"+host.getIP()+":10080","/usr/local/hadoop/etc/hadoop","/yarn-site.xml",yarnlist)==0){//调用名为hadoop的RPC
            log.error(host.getIP()+" : "+"The /yarn-site.xml was configured which is failure.");
        }
        hc.shutdownMonitor(hostlist);//关闭ZooKeeperMonitor的jar线程
        log.info(host.getIP()+" : "+"The hadoop has deployed.");
    }
}
