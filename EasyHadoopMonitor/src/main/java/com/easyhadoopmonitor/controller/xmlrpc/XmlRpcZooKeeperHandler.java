/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyhadoopmonitor.controller.xmlrpc;

import java.util.Map;


/**
 *
 * @author sheriff
 */
public class XmlRpcZooKeeperHandler {
    
    private ZooKeeperConfigController zcc=new ZooKeeperConfigController();
    public int createCfg(String dir,String filename){
        return zcc.createConfig(dir, filename);
    }
    
    public int writeCfg(String dir,String filename,Map<String,String> propermap){
        return zcc.writeConfig(dir, filename,propermap);
    }
}
