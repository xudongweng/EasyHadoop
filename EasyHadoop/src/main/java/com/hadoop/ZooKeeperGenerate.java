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
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class ZooKeeperGenerate {
    public static void main(String[] args){
        
        Logger log=Logger.getLogger(ZooKeeperGenerate.class);
        LoadConfigController lcc=new LoadConfigController();
        if(lcc.loadFile("config.properties")==0)return;
        
        InstallFiles files=lcc.getInstallFiles();

        List<LinuxHost> hostlist=lcc.getcfgHosts();

        ZooKeeperController zkc=new ZooKeeperController();
        zkc.configZooKeeper(hostlist, files.getZooKeeper());//上传zookeeper安装文件
        
        if(lcc.loadFile("zoo.properties")==0)return;
        Map<String,String> zoomap=lcc.getZoo();//加载部分zookeeper配置参数
        
        int i=0;
        SSHLinuxHelper ssh=new SSHLinuxHelper();
        for(LinuxHost host:hostlist){
            host.setHostname(ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),host.getPort(), "hostname"));//获取每台服务器主机名
            if(host.getHostname().equals("")){
                log.error(host.getIP()+" : "+"hostname is null.");
                return;
            }
            //System.out.println(host.getHostname());
            zoomap.put("server."+i, host.getHostname()+":2888:3888");//加载剩余zookeeper配置参数
            i++;
        }
        
        LinuxHost host=hostlist.get(0);
        zkc.configfile(hostlist, files.getMonitor());
        
        //System.out.println(map.toString());
        RPCClientController rpcClient=new RPCClientController();
        int s=rpcClient.invokeZooCreate("http://"+host.getIP()+":10080","/usr/local/zookeeper/conf","zoo.cfg");
        System.out.println(s);
    }
}
