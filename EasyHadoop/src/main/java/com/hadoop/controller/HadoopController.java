/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.controller;

import com.hadoop.helper.SSHLinuxHelper;
import com.hadoop.model.LinuxHost;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class HadoopController {
    private final Logger log=Logger.getLogger(HadoopController.class);
    private final SSHLinuxHelper ssh=new SSHLinuxHelper();
    private final Properties prop = new Properties();
    private final String dst="/tmp";
    public int loadFile(String cfgfile){
        File file = new File(cfgfile);
        if(!file.exists()){
            log.error("File "+file.getAbsolutePath()+cfgfile+" is not exist.");
            return 0;
        }
        //加载配置
        try{
            prop.load(new FileInputStream(cfgfile));
        }catch(IOException e){
            log.error(e.toString()+" [cfgfile]："+cfgfile);
            return 0;
        }
        return 1;
    }
    
    public void configHadoop(List<LinuxHost> hostlist,String dir){
        LinuxHost host=hostlist.get(0);
        String filename=dir.substring(dir.lastIndexOf(File.separator)+1,dir.length());
        log.info(host.getIP()+" : Upload "+dir+" to "+host.getIP()+".");
        ssh.uploadfile(host.getIP(), host.getUser(), host.getPassword(), dir, dst);//上传hadoop
        log.info(host.getIP()+" : Uncompress "+filename+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd "+dst+" \n tar zxf "+filename);//解压hadoop
        String hadoopfile=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "ls "+dst+" |grep hadoop |grep -v gz ");
        //System.out.println(hadoopfile);
        log.info(host.getIP()+" : clear "+dst+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "rm -rf /usr/local/hadoop \n mv "+dst+"/"+hadoopfile +" /usr/local/hadoop \n rm -rf "+dst+"/*.gz");//移动解压后的hadoop，删除上传文件
        log.info(host.getIP()+" : set environment of .bashrc.");
        if(!ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"cd ~ \n cat .bashrc").contains("HADOOP_HOME"))//如果不包含JAVA_HOME
        {
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd ~ \n echo \"HADOOP_HOME=/usr/local/hadoop export HADOOP_HOME \" >> .bashrc \n echo \"PATH=\\$PATH:\\$HADOOP_HOME/bin export PATH\" >> .bashrc \n source .bashrc");
        }
    }
    
    
    public List<String> getCfg(){
        Enumeration<Object> keys = prop.keys();
        List<String> l=new ArrayList<>();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            //System.out.println(key + "=" + prop.getProperty(key));
            l.add(key+"="+prop.getProperty(key));
        }
        prop.clear();
        return l;
    }
    
    public void test(List<LinuxHost> hostlist){
        LinuxHost host=hostlist.get(0);
        String myname=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "hostname");
        System.out.println(myname);
    }
}
