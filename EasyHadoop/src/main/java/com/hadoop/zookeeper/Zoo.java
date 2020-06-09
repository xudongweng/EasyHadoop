/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.zookeeper;

/**
 *
 * @author sheriff
 */
public class Zoo {
    private int tickTime=2000;
    private int initLimit=10;
    private int syncLimit=5;
    private String dataDir="/usr/local/zookeeper/data";
    private int clientPort=2181;
    
    public void setTickTime(int tickTime){
        this.tickTime=tickTime;
    }
    
    public int getTickTime(){
        return this.tickTime;
    }
    
    public void setInitLimit(int initLimit){
        this.initLimit=initLimit;
    }
    
    public int getInitLimit(){
        return this.initLimit;
    }
    
    public void setSyncLimit(int syncLimit){
        this.syncLimit=syncLimit;
    }
    
    public int getSyncLimit(){
        return this.syncLimit;
    }
    
    public void setClientPort(int clientPort){
        this.clientPort=clientPort;
    }
    
    public int getClientPort(){
        return this.clientPort;
    }
    
    public void setDataDir(String dataDir){
        this.dataDir=dataDir;
    }
    
    public String getDataDir(){
        return this.dataDir;
    }
}
