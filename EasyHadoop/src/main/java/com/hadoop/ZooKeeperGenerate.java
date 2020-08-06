/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop;

import com.hadoop.controller.LoadConfigController;
import com.hadoop.controller.RPCClientController;
import com.hadoop.controller.ZooKeeperController;
import com.hadoop.helper.SSHLinuxHelper;
import com.hadoop.model.InstallFiles;
import com.hadoop.model.LinuxHost;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class ZooKeeperGenerate {
    public static void main(String[] args){
        
        Logger log=Logger.getLogger(ZooKeeperGenerate.class);
        LoadConfigController lcc=new LoadConfigController();
        int deploy=lcc.loadFile("config.properties");//sameDeploy返回i值，deploy=-1为错误，0为部署第一台服务器，1为部署全部服务器
        
        InstallFiles files=lcc.getInstallFiles();

        List<LinuxHost> hostlist=lcc.getcfgHosts();

        ZooKeeperController zkc=new ZooKeeperController();

        if(zkc.loadFile("zoo.properties")==0)return;
        List<String> zoolist=zkc.getZoo();//加载部分zookeeper配置参数
        
        int i=0;
        SSHLinuxHelper ssh=new SSHLinuxHelper();
        for(LinuxHost host:hostlist){
            host.setHostname(ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),host.getPort(), "hostname"));//获取每台服务器主机名
            if(host.getHostname().equals("")){
                log.error(host.getIP()+" : "+"The hostname is null.");
                return;
            }
            //System.out.println(host.getHostname());
            zoolist.add("server."+i+"="+host.getHostname()+":2888:3888");//加载剩余zookeeper配置参数
            i++;
            zkc.addHostsList(host.getIP(), host.getHostname());
        }
        
        if(deploy==0){
            LinuxHost host=hostlist.get(0);
            zkc.configZooKeeper(host, files.getZooKeeper());//上传zookeeper安装文件
            zkc.shutdownMonitor(host);//确保之前没有jar的线程，如果有则删除。
            zkc.uploadMonitor(host, files.getMonitor());//上传ZooKeeperMonitor的jar

            //System.out.println(map.toString());
            RPCClientController rpcClient=new RPCClientController();
            //执行zookeeper配置文件的写入
            if(rpcClient.invokeZooWrite("http://"+host.getIP()+":10080","/usr/local/zookeeper/conf","zoo.cfg",zoolist)==0){//调用名为zookeeper的RPC
                log.error(host.getIP()+" : "+"The zoo.cfg was configured which is failure.");
            }
            zkc.shutdownMonitor(host);//关闭ZooKeeperMonitor的jar线程
            log.info(host.getIP()+" : "+"The zookeeper has deployed.");
        }else{
            for(LinuxHost host:hostlist){
                zkc.configZooKeeper(host, files.getZooKeeper());//上传zookeeper安装文件
                zkc.shutdownMonitor(host);//确保之前没有jar的线程，如果有则删除。
                zkc.uploadMonitor(host, files.getMonitor());//上传ZooKeeperMonitor的jar

                //System.out.println(map.toString());
                RPCClientController rpcClient=new RPCClientController();
                //执行zookeeper配置文件的写入
                if(rpcClient.invokeZooWrite("http://"+host.getIP()+":10080","/usr/local/zookeeper/conf","zoo.cfg",zoolist)==0){//调用名为zookeeper的RPC
                    log.error(host.getIP()+" : "+"The zoo.cfg was configured which is failure.");
                }
                zkc.shutdownMonitor(host);//关闭ZooKeeperMonitor的jar线程
                log.info(host.getIP()+" : "+"The zookeeper has deployed.");
            }
        }
    }
    
    
}
