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
        if(lcc.loadFile("config.properties")==0)return;
        List<LinuxHost> hostlist=lcc.getcfgHosts();
        InstallFiles files=lcc.getInstallFiles();
        lcc.close();
        JDKController jc=new JDKController();
        jc.configJDK(hostlist,files.getJDK());//上传jdk安装文件
    }
}
