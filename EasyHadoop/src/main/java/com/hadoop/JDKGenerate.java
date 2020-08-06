/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop;

import com.hadoop.controller.JDKController;
import com.hadoop.controller.LoadConfigController;
import com.hadoop.model.InstallFiles;
import com.hadoop.model.LinuxHost;
import java.util.List;

/**
 *
 * @author sheriff
 */
public class JDKGenerate {
    public static void main(String[] args){
        LoadConfigController lcc=new LoadConfigController();
        int deploy=lcc.loadFile("config.properties");//sameDeploy返回i值，deploy=-1为错误，0为部署第一台服务器，1为部署全部服务器
        if(deploy==-1)return;
        List<LinuxHost> hostlist=lcc.getcfgHosts();
        InstallFiles files=lcc.getInstallFiles();
        lcc.close();
        JDKController jc=new JDKController();
        if(deploy==0)
            jc.configJDK(hostlist.get(0),files.getJDK());//上传jdk安装文件
        else{
            for(LinuxHost host:hostlist){
                jc.configJDK(host,files.getJDK());
            }
        }
    }
}
