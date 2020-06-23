/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.controller;

import com.hadoop.helper.SSHLinuxHelper;
import com.hadoop.model.LinuxHost;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class ZooKeeperController {
    private Logger log=Logger.getLogger(ZooKeeperController.class);
    public void configZooKeeper(List<LinuxHost> hostlist,String filepath){
        SSHLinuxHelper ssh=new SSHLinuxHelper();
        LinuxHost host=hostlist.get(0);
        String dst="/tmp";
        //String filepath=files.getZooKeeper();
        String filename=filepath.substring(filepath.lastIndexOf(File.separator)+1,filepath.length());
        log.info(host.getHostname()+" : Upload "+filepath+" to "+host.getHostname()+".");
        ssh.uploadfile(host.getIP(), host.getUser(), host.getPassword(), filepath, dst);//上传zookeeper
        log.info(host.getHostname()+" : Uncompress "+filename+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd "+dst+" \n tar zxf "+filename);//解压zookeeper
        String zoofile=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "ls "+dst+" |grep zookeeper |grep -v gz ");
        log.info(host.getHostname()+" : clear "+dst+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "rm -rf /usr/local/zookeeper \n mv "+dst+"/"+zoofile +" /usr/local/zookeeper \n rm -rf "+dst+"/*.gz");//移动解压后的zookeeper，删除上传文件
        log.info(host.getHostname()+" : set environment of .bashrc.");
        if(!ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"cd ~ \n cat .bashrc").contains("ZOOKEEPER_HOME"))//如果不包含JAVA_HOME
        {
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd ~ \n echo \"ZOOKEEPER_HOME=/usr/local/zookeeper export JAVA_HOME \" >> .bashrc \n echo \"PATH=\\$PATH:\\$ZOOKEEPER_HOME/bin export PATH\" >> .bashrc \n source .bashrc");
        }
    }
}
