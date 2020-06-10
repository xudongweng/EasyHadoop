/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop;

import com.hadoop.controller.LoadConfigController;
import com.hadoop.controller.helper.SSHLinuxHelper;
import com.hadoop.model.InstallFiles;
import com.hadoop.model.LinuxHost;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class JDKGenerate {
    public static void main(String[] args){
        Logger log=Logger.getLogger(JDKGenerate.class);
        LoadConfigController lcc=new LoadConfigController();
        if(lcc.loadFile("config.properties")==0)return;
        List<LinuxHost> hostlist=lcc.getcfgHosts();
        InstallFiles files=lcc.getInstallFiles();
        SSHLinuxHelper ssh=new SSHLinuxHelper();
        LinuxHost host=hostlist.get(0);
        String dst="/tmp";
        String filename=files.getJDK().substring(files.getJDK().lastIndexOf(File.separator)+1,files.getJDK().length());
        ssh.uploadfile(host.getIP(), host.getUser(), host.getPassword(), files.getJDK(), dst);//上传jdk
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd "+dst+" \n tar zxf "+filename);//解压jdk
        String jdkfile=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "ls "+dst+" |grep jdk |grep -v gz ");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "rm -rf /usr/local/jdk \n mv "+dst+"/"+jdkfile +" /usr/local/jdk \n rm -rf "+dst+"/*.gz");//移动解压后的jdk，删除上传文件
        if(!ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"cd ~ \n cat .bashrc").contains("JAVA_HOME"))//如果不包含JAVA_HOME
        {
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd ~ \n echo \"JAVA_HOME=/usr/local/jdk export JAVA_HOME \" >> .bashrc \n echo \"PATH=\\$PATH:\\$JAVA_HOME/bin export PATH\" >> .bashrc \n source .bashrc");
        }
    }
}
