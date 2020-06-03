/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop;

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
        SSHlinuxHelper ssh=new SSHlinuxHelper();
        
        for(LinuxHost host:hostlist){
            //验证ssh-keygen
            String authorized_keys=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),host.getPort(), "cd ~ \n cat .ssh/authorized_keys");
            //判断authorized_keys是否原本就存在
            if(authorized_keys.equals("")||authorized_keys.contains("cat .ssh/authorized_keys")){
                log.info(host.getIP()+":"+".ssh/authorized_keys is not exist.");
                String id_rsa=ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),host.getPort(), "cd ~ \n cat .ssh/id_rsa.pub");
                //检查id_rsa.pub是否存在，生成新的authorized_keys文件
                if(id_rsa.contains("cat .ssh/id_rsa.pub")||id_rsa.equals("")){
                    log.info(host.getIP()+" : "+" .ssh/id_rsa.pub is not exist.");
                    return;
                }
                ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),host.getPort(), "cd ~ \n mv .ssh/id_rsa.pub .ssh/authorized_keys");
                log.info(host.getIP()+" : "+"mv .ssh/ id_rsa.pub .ssh/authorized_keys.");
            }
            host.setHostname(ssh.execCmd(host.getIP(), host.getUser(), host.getPassword(),host.getPort(), "hostname"));
            //将单个文件authorized_keys的多个key拆分成哈希表，进行后期验证
            while(authorized_keys.length()>381){
                //System.out.println(authorized_keys.indexOf("ssh-rsa",381));
                int i=authorized_keys.indexOf("ssh-rsa",381);
                if(i<0)i=authorized_keys.length();
                //System.out.println(authorized_keys.substring(authorized_keys.indexOf("@")+1,i));
                //System.out.println(authorized_keys.substring(0,i));
                host.putKey(authorized_keys.substring(authorized_keys.indexOf("@")+1,i),authorized_keys.substring(0,i));
                authorized_keys=authorized_keys.substring(i,authorized_keys.length());
            }
            //System.out.println(host.getHostname());
            //System.out.println(authorized_keys);
            //System.out.println(authorized_keys.length());
        }
        //将一台服务器轮流与其他服务器ssh-keygen进行对比，
        //1)对比主机名是否存在响应的ssh-keygen
        //2)存在主机名相同的情况下，ssh-keygen是否相同
        for(LinuxHost host1:hostlist){
             for(LinuxHost host2:hostlist){
                 if(!host1.getIP().equals(host2.getIP())){
                     Iterator iter = host2.getKeys().entrySet().iterator();
                     while (iter.hasNext()) {
                         Map.Entry entry = (Map.Entry) iter.next();
                         System.out.println(entry.getValue().toString());
                         System.out.println(host1.getValue(entry.getKey().toString()));
                         if(host1.getValue(entry.getKey().toString())==null){//为找到相同主机名
                             
                         }else if(!host1.getValue(entry.getKey().toString()).equals(entry.getValue().toString())){//主机名相同，但ssh-keygen不同
                             log.warn(host1.getIP()+" : "+host1.getIP()+" and "+host2.getIP()+"'s hostname is same,but key is different.["+host1.getValue(entry.getKey().toString())+"]["+
                                     host1.getValue(entry.getKey().toString())+"]");
                             
                         }
                     }
                     System.out.println("-----------------------------");
                 }
             }
        }
    }
}
