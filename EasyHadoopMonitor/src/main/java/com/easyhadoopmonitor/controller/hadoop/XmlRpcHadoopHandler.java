/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyhadoopmonitor.controller.hadoop;

import java.util.List;

/**
 *
 * @author sheriff
 */
public class XmlRpcHadoopHandler {
    private final HadoopConfigController hcc=new HadoopConfigController();
    public int createCfg(String dir,String filename){
        return hcc.createCfg(combine(dir, filename));
    }
    
    public int writeCfg(String dir,String filename,List<String> contestlist){
        return hcc.writeConfig(combine(dir, filename),contestlist);
    }
    
    private String combine(String dir,String filename){
        if(dir.lastIndexOf("/")==dir.length()-1)
            return dir+filename;
        else 
            return dir+"/"+filename;
    }
}
