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
        int deploy=lcc.loadFile("config.properties");//sameDeploy返回i值，deploy=-1为错误，0为部署第一台服务器，1为部署全部服务器
        if(deploy==-1)return;
        InstallFiles files=lcc.getInstallFiles();
        if(!lcc.fileExist(files.getHadoop())){ 
            log.error(files.getHadoop() +" is not exist.");
            return;
        }
        List<LinuxHost> hostlist=lcc.getcfgHosts();
        
        HadoopController hc=new HadoopController();

        hc.setHostnameList(hostlist);//主机名列表生成
 
        List<String> corelist=hc.loadCfg("core.properties");
        List<String> hdfslist=hc.loadCfg("hdfs.properties");
        List<String> mapredlist=hc.loadCfg("mapred.properties");
        List<String> yarnlist=hc.loadCfg("yarn.properties");
        if(corelist==null||hdfslist==null||mapredlist==null||yarnlist==null)return;
   
        if(deploy==0){
            LinuxHost host=hostlist.get(0);
            
            hc.configHadoop(host, files.getHadoop());
            
            hc.shutdownMonitor(host);
            hc.uploadMonitor(host, files.getMonitor());//上传HadoopMonitor的jar

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
            hc.shutdownMonitor(host);//关闭ZooKeeperMonitor的jar线程
            log.info(host.getIP()+" : "+"The hadoop has deployed.");
        }else{
            for(LinuxHost host:hostlist){
                hc.configHadoop(host, files.getHadoop());
                
                hc.shutdownMonitor(host);
                hc.uploadMonitor(host, files.getMonitor());//上传HadoopMonitor的jar

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
                hc.shutdownMonitor(host);//关闭ZooKeeperMonitor的jar线程
                log.info(host.getIP()+" : "+"The hadoop has deployed.");
            }
        }
    }
}
