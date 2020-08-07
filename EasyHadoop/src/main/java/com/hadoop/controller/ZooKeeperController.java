/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.controller;

import com.hadoop.helper.OrderedProperties;
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
public class ZooKeeperController {
    private final Logger log=Logger.getLogger(ZooKeeperController.class);
    private final SSHLinuxHelper ssh=new SSHLinuxHelper();
    private final Properties prop = new OrderedProperties();//解决读取文件乱序
    private String jarfilename;
    private final String dst="/tmp";
    private final List<String> hostsList=new ArrayList<>();
    
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
            log.error(e.toString()+" [cfgfile]:"+cfgfile);
            return 0;
        }
        return 1;
    }
    
    public List<String> getZoo(){
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
    
    public void addHostsList(String ip,String hostname){
        this.hostsList.add(ip+" "+hostname);
    }
    
    public void configZooKeeper(LinuxHost host,String dir){
        //String filepath=files.getZooKeeper();
        String filename=dir.substring(dir.lastIndexOf(File.separator)+1,dir.length());
        log.info(host.getIP()+" : Upload "+dir+" to "+host.getIP()+".");
        ssh.uploadfile(host.getIP(), host.getUser(), host.getPassword(), dir, dst);//上传zookeeper
        log.info(host.getIP()+" : Uncompress "+filename+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd "+dst+" \n tar zxf "+filename);//解压zookeeper
        String zoofile=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "ls "+dst+" |grep zookeeper |grep -v gz ");
        log.info(host.getIP()+" : clear "+dst+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "rm -rf /usr/local/zookeeper \n mv "+dst+"/"+zoofile +" /usr/local/zookeeper \n rm -rf "+dst+"/*.gz");//移动解压后的zookeeper，删除上传文件
        log.info(host.getIP()+" : set environment of .bashrc.");
        if(!ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"cd ~ \n cat .bashrc").contains("ZOOKEEPER_HOME"))//如果不包含JAVA_HOME
        {
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd ~ \n echo \"ZOOKEEPER_HOME=/usr/local/zookeeper export ZOOKEEPER_HOME \" >> .bashrc \n echo \"PATH=\\$PATH:\\$ZOOKEEPER_HOME/bin export PATH\" >> .bashrc \n source .bashrc");
        }
        
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "echo \"127.0.0.1 localhost\">/etc/hosts");
        for(String hostsString:hostsList){
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "echo \""+hostsString+"\">>/etc/hosts");
        }
    }
    
    public void uploadMonitor(LinuxHost host,String monitorpath){
        this.jarfilename=monitorpath.substring(monitorpath.lastIndexOf(File.separator)+1,monitorpath.length());
        log.info(host.getIP()+" : Upload "+monitorpath+" to "+host.getIP()+".");
        ssh.uploadfile(host.getIP(), host.getUser(), host.getPassword(), monitorpath, dst);//上传EasyHadoopMonitor.jar
        log.info(host.getIP()+" : Running "+this.jarfilename+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd "+dst+" \n nohup java -cp "+jarfilename+" com.easyhadoopmonitor.ZooKeeperMonitor >/dev/null 2>&1 &");//运行上传jar包
    }
    
    public void shutdownMonitor(LinuxHost host){

        log.info(host.getIP()+" : Find ZooKeeperMonitor Pid.");
        String wc=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"jps|grep ZooKeeperMonitor|wc -l");
        if(!wc.equals("0"))
        {
            log.info(host.getIP()+" : Shutdown monitor.");
            String monitorPid=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"jps |grep ZooKeeperMonitor| awk '{print $1}'");
            //System.out.println(monitorPid);
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"kill -9 "+monitorPid);
        }
        log.info(host.getIP()+" : Clear "+dst+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"rm -rf "+dst+"/*.jar");
    }
}
