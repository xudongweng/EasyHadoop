/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop;

import com.hadoop.controller.LoadConfigController;
import com.hadoop.controller.helper.SSHlinuxHelper;
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
        lcc.loadFile();
        List<LinuxHost> hostlist=lcc.getcfgHosts();
        SSHlinuxHelper ssh=new SSHlinuxHelper();
        
        for(LinuxHost host:hostlist){
            String id_rsa=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),host.getPort(), "cd ~ \n cat .ssh/id_rsa.pub");
            if(id_rsa.contains("cat .ssh/id_rsa.pub")||id_rsa.equals("")){
                log.info(" .ssh/id_rsa.pub is not exist.");
                return;
            }
            String authorized_keys=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),host.getPort(), "cd ~ \n cat .ssh/authorized_keys");
            if(authorized_keys.equals("")||authorized_keys.contains("cat .ssh/authorized_keys")){
                log.info(".ssh/authorized_keys is not exist.");
            }
        }
    }
}
