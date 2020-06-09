/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop;

import com.hadoop.controller.LoadConfigController;
import com.hadoop.controller.helper.SSHLinuxHelper;
import com.hadoop.model.LinuxHost;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class ZookeeperGenerate {
    public static void main(String[] args){
        Logger log=Logger.getLogger(ZookeeperGenerate.class);
        LoadConfigController lcc=new LoadConfigController();
        if(lcc.loadFile("zoo.properties")==0)return;
        List<LinuxHost> hostlist=lcc.getcfgHosts();
        SSHLinuxHelper ssh=new SSHLinuxHelper();
        
        for(LinuxHost host:hostlist){
            host.setHostname(ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),host.getPort(), "hostname"));
            if(host.getHostname().equals("")){
                log.error(host.getIP()+" : "+"hostname is null.");
                return;
            }
            System.out.println(host.getHostname());
        }
    }
}
