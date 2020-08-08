/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.controller;

import com.hadoop.helper.SSHLinuxHelper;
import com.hadoop.model.LinuxHost;
import java.io.File;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class JDKController {
    private final Logger log=Logger.getLogger(JDKController.class);
    private final SSHLinuxHelper ssh=new SSHLinuxHelper();
    public void configJDK(LinuxHost host,String dir){
        
        String dst="/tmp";
        String filename="";
        if(dir.contains("\\"))
            filename=dir.substring(dir.lastIndexOf("\\")+1,dir.length());
        else if(dir.contains("/"))
            filename=dir.substring(dir.lastIndexOf("/")+1,dir.length());
        log.info(host.getIP()+" : Upload "+dir+" to "+host.getIP()+".");
        ssh.uploadfile(host.getIP(), host.getUser(), host.getPassword(), dir, dst);//上传jdk
        log.info(host.getIP()+" : Uncompress "+filename+".");
        log.info(host.getIP()+" :cd "+dst+" \n tar zxf "+filename);
        if(ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd "+dst+" \n tar zxf "+filename).contains("bash:")){//解压jdk
            log.error(host.getIP()+": Command(cd "+dst+" \n tar zxf "+filename+") is error");
            return;
        }
        String jdkfile=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "ls "+dst+" |grep jdk |grep -v gz ");
        log.info(host.getIP()+" : Clear "+dst+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "rm -rf /usr/local/jdk \n mv "+dst+"/"+jdkfile +" /usr/local/jdk \n rm -rf "+dst+"/*.gz");//移动解压后的jdk，删除上传文件
        log.info(host.getIP()+" : set environment of .bashrc.");
        if(!ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"cd ~ \n cat .bashrc").contains("JAVA_HOME"))//如果不包含JAVA_HOME
        {
            log.info(host.getIP()+" : set JAVA_HOME.");
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd ~ \n echo \"JAVA_HOME=/usr/local/jdk export JAVA_HOME \" >> .bashrc");
            log.info(host.getIP()+" : set CLASSPATH.");
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd ~ \n echo \"CLASSPATH=.:\\$JAVA_HOME/jre/lib/rt.jar:\\$JAVA_HOME/lib/dt.jar:\\$JAVA_HOME/lib/tools.jar export CLASSPATH \" >> .bashrc");
            log.info(host.getIP()+" : set PATH.");
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd ~ \n echo \"PATH=\\$PATH:\\$JAVA_HOME/bin export PATH\" >> .bashrc \n source .bashrc");
        }
        log.info(host.getIP()+" : JDK has deployed.");
    }
}
