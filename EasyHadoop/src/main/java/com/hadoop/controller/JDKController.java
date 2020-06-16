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
public class JDKController {
    private Logger log=Logger.getLogger(JDKController.class);
    public void configJDK(List<LinuxHost> hostlist,String filepath){
        SSHLinuxHelper ssh=new SSHLinuxHelper();
        LinuxHost host=hostlist.get(0);
        String dst="/tmp";
        String filename=filepath.substring(filepath.lastIndexOf(File.separator)+1,filepath.length());
        log.info(host.getHostname()+" : Upload "+filepath+" to "+host.getHostname()+".");
        ssh.uploadfile(host.getIP(), host.getUser(), host.getPassword(), filepath, dst);//上传jdk
        log.info(host.getHostname()+" : Uncompress "+filename+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd "+dst+" \n tar zxf "+filename);//解压jdk
        String jdkfile=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "ls "+dst+" |grep jdk |grep -v gz ");
        log.info(host.getHostname()+" : clear "+dst+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "rm -rf /usr/local/jdk \n mv "+dst+"/"+jdkfile +" /usr/local/jdk \n rm -rf "+dst+"/*.gz");//移动解压后的jdk，删除上传文件
        log.info(host.getHostname()+" : set environment of .bashrc.");
        if(!ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"cd ~ \n cat .bashrc").contains("JAVA_HOME"))//如果不包含JAVA_HOME
        {
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd ~ \n echo \"JAVA_HOME=/usr/local/jdk export JAVA_HOME \" >> .bashrc \n echo \"PATH=\\$PATH:\\$JAVA_HOME/bin export PATH\" >> .bashrc \n source .bashrc");
        }
        log.info(host.getHostname()+" : JDK has deployed.");
    }
}
