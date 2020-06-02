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
public class LinuxHost {
    private String hostname="localhost";
    private String ip="127.0.0.1";
    private String user="root";
    private String password="123456";
    private int port=22;
    
    public LinuxHost(){}
    public LinuxHost(String hostname,String ip,String user,String password,int port){
        this.hostname=hostname;
        this.ip=ip;
        this.user=user;
        this.password=password;
        this.port=port;
    }
    
    
    public void setHostname(String hostname){
        this.hostname=hostname;
    }
    
    public void setIP(String ip){
        this.ip=ip;
    }
    
    public void setUser(String user){
        this.user=user;
    }
    
    public void setPassword(String password){
        this.password=password;
    }
    
    public void setPort(int port){
        this.port=port;
    }
    
    public String getHostname(){
        return this.hostname;
    }
    
    public String getIP(){
        return this.ip;
    }
    
    public String getUser(){
        return this.user;
    }
    
    public String getPassword(){
        return this.password;
    }
    
    public int getPort(){
        return this.port;
    } 
}
