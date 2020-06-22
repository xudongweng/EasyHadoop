/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop;

import com.hadoop.controller.LoadConfigController;
import com.hadoop.helper.SSHLinuxHelper;
import com.hadoop.model.InstallFiles;
import com.hadoop.model.LinuxHost;
import java.io.File;
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
        if(lcc.loadFile("config.properties")==0)return;
        
        InstallFiles files=lcc.getInstallFiles();
        
        List<LinuxHost> hostlist=lcc.getcfgHosts();
        SSHLinuxHelper ssh=new SSHLinuxHelper();
        
        for(LinuxHost host:hostlist){
            host.setHostname(ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),host.getPort(), "hostname"));
            if(host.getHostname().equals("")){
                log.error(host.getIP()+" : "+"hostname is null.");
                return;
            }
            //System.out.println(host.getHostname());
        }
        
        LinuxHost host=hostlist.get(0);
        String dst="/tmp";
        String filepath=files.getZooKeeper();
        String filename=filepath.substring(filepath.lastIndexOf(File.separator)+1,filepath.length());
        log.info(host.getHostname()+" : Upload "+filepath+" to "+host.getHostname()+".");
        ssh.uploadfile(host.getIP(), host.getUser(), host.getPassword(), filepath, dst);
        log.info(host.getHostname()+" : Uncompress "+filename+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd "+dst+" \n tar zxf "+filename);
        String zoofile=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "ls "+dst+" |grep jdk |grep -v gz ");
        log.info(host.getHostname()+" : clear "+dst+".");
        //if(lcc.loadFile("zoo.properties")==0)return;
        //lcc.getZoo();
        
    }
}
