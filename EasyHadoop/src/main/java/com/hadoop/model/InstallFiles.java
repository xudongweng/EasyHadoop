/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.model;

/**
 *
 * @author sheriff
 */
public class InstallFiles {
    String jdk="";
    String zookeeper="";
    String hadoop="";
    String monitor="";
    public void setJDK(String jdk){
        this.jdk=jdk;
    }
    public String getJDK(){
        return this.jdk;
    }
    
    public void setZooKeeper(String zookeeper){
        this.zookeeper=zookeeper;
    }
    public String getZooKeeper(){
        return this.zookeeper;
    }
    
    public void setHadoop(String hadoop){
        this.hadoop=hadoop;
    }
    public String getHadoop(){
        return this.hadoop;
    }
    
    public void setMonitor(String monitor){
        this.monitor=monitor;
    }
    public String getMonitor(){
        return this.monitor;
    }
}
