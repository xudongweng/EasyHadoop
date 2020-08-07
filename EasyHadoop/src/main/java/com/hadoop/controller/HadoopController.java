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
public class HadoopController {
    private final Logger log=Logger.getLogger(HadoopController.class);
    private final SSHLinuxHelper ssh=new SSHLinuxHelper();
    private final Properties prop = new OrderedProperties();//解决读取文件乱序
    private final String dst="/tmp";
    private final List<String> hostnamelist=new ArrayList<>();
    private String jarfilename;
    
    public List<String> loadCfg(String cfgfile){
        Properties prop = new OrderedProperties();
        File file = new File(cfgfile);
        if(!file.exists()){
            log.error("File "+file.getAbsolutePath()+cfgfile+" is not exist.");
            return null;
        }
        //加载配置
        try{
            prop.load(new FileInputStream(cfgfile));
        }catch(IOException e){
            log.error(e.toString()+" [cfgfile]："+cfgfile);
            return null;
        }
        
        
        Enumeration<Object> keys = prop.keys();
        List<String> list=new ArrayList<>();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            //System.out.println(key + "=" + prop.getProperty(key));
            list.add(key+"="+this.replaceHostnames(prop.getProperty(key)));
        }
        prop.clear();
        return list;
    }
    
    public void configHadoop(LinuxHost host,String dir){

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
        
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"rm -rf /usr/local/hadoop/etc/hadoop/slaves");
        for(String hostname:hostnamelist){
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"echo \""+hostname+"\" >>/usr/local/hadoop/etc/hadoop/slaves");
        }
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"cp /usr/local/hadoop/etc/hadoop/mapred-site.xml.template /usr/local/hadoop/etc/hadoop/mapred-site.xml");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"sed -i 's/\\${JAVA_HOME}/\\/usr\\/local\\/jdk/g'  /usr/local/hadoop/etc/hadoop/hadoop-env.s");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"sed -i '1i\\export JAVA_HOME=\\/usr\\/local\\/jdk\\/' /usr/local/hadoop/etc/hadoop/yarn-env.sh");
    }
    
    public void setHostnameList(List<LinuxHost> hostlist){
        //获取每台服务器的主机名
        for(LinuxHost hosts:hostlist){
             hostnamelist.add(ssh.execCmd(hosts.getIP(), hosts.getUser(), hosts.getPassword(), "hostname"));
        }
    }
    
    private String replaceHostnames(String val){
        if(val.contains("${host")){
            int i=1;
            for(String hostname:hostnamelist){
                if(val.contains("${host"+i+"}")) val=val.replace("${host"+i+"}", hostname);
                i++;
            }
        }
        return val;
    }
    
    public void uploadMonitor(LinuxHost host,String monitorpath){
        this.jarfilename=monitorpath.substring(monitorpath.lastIndexOf(File.separator)+1,monitorpath.length());
        log.info(host.getIP()+" : Upload "+monitorpath+" to "+host.getIP()+".");
        ssh.uploadfile(host.getIP(), host.getUser(), host.getPassword(), monitorpath, dst);//上传EasyHadoopMonitor.jar
        log.info(host.getIP()+" : Running "+this.jarfilename+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(), "cd "+dst+" \n nohup java -cp "+jarfilename+" com.easyhadoopmonitor.HadoopMonitor >/dev/null 2>&1 &");//运行上传jar包
    }
    
    public void shutdownMonitor(LinuxHost host){
        log.info(host.getIP()+" : Find HadoopMonitor Pid.");
        String wc=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"jps|grep HadoopMonitor|wc -l");
        if(!wc.equals("0"))
        {
            log.info(host.getIP()+" : Shutdown monitor.");
            String monitorPid=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"jps |grep HadoopMonitor| awk '{print $1}'");
            //System.out.println(monitorPid);
            ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"kill -9 "+monitorPid);
        }
        log.info(host.getIP()+" : Clear "+dst+".");
        ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),"rm -rf "+dst+"/*.jar");
    }
}
