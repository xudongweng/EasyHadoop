/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop;

import com.hadoop.controller.HostConfigController;
import com.hadoop.controller.LoadConfigController;
import com.hadoop.controller.helper.SSHlinuxHelper;
import com.hadoop.model.LinuxHost;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class SSHDeploy {
    public static void main(String[] args){
        Logger log=Logger.getLogger(SSHDeploy.class);
        LoadConfigController lcc=new LoadConfigController();
        lcc.loadFile();
        List<LinuxHost> hostlist=lcc.getcfgHosts();
        HostConfigController bcc=new HostConfigController();
        bcc.configSSH(hostlist);
    }
}
