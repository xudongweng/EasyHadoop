/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop;

import com.hadoop.controller.SSHConfigController;
import com.hadoop.controller.LoadConfigController;
import com.hadoop.model.LinuxHost;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class SSHDeploy {
    public static void main(String[] args){
        Logger log=Logger.getLogger(SSHDeploy.class);
        LoadConfigController lcc=new LoadConfigController();
        if(lcc.loadFile("config.properties")==0)return;
        List<LinuxHost> hostlist=lcc.getcfgHosts();
        SSHConfigController bcc=new SSHConfigController();
        bcc.configSSH(hostlist);
    }
}
