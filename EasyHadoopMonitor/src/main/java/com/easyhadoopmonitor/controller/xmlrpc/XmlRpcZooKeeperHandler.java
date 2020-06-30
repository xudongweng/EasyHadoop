/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyhadoopmonitor.controller.xmlrpc;

import java.util.List;


/**
 *
 * @author sheriff
 */
public class XmlRpcZooKeeperHandler {
    
    private final ZooKeeperConfigController zcc=new ZooKeeperConfigController();
    public int createCfg(String dir,String filename){
        return zcc.createConfig(dir, filename);
    }
    
    public int writeCfg(String dir,String filename,List<String> proplist){
        return zcc.writeConfig(dir, filename,proplist);
    }
}
