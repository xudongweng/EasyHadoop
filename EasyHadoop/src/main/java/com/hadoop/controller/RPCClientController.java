/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author sheriff
 */
public class RPCClientController {
    private final Logger log=Logger.getLogger(RPCClientController.class);

    public int invokeZooWrite(String url,String dir,String filename,List<String> proplist){
        int result =0;
        // XmlRpcClient
        XmlRpcClient client = new XmlRpcClient();
        // 客户端配置
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
                // 访问服务器路径、端口
            config.setServerURL(new URL(url));	//此10080端口正好被内嵌webServer监听；
            //config.setServerURL(new URL("http://localhost:10080"));
        } catch (MalformedURLException e) {
            log.error(e.toString()+" [url]:"+url+",[dir]:"+dir+",[filename]:"+filename+",[proplist]:"+proplist.toString());
            return result;
        }
        // 客户端设置
        client.setConfig(config);
        // 远程调用 
        try {
            result = (int) client.execute("ZooKeeper.writeCfg", new Object[] { dir,filename,proplist });
        } catch (XmlRpcException e) {
            log.error(e.toString()+" [url]:"+url+",[dir]:"+dir+",[filename]:"+filename+",[proplist]:"+proplist.toString());
        }
        return result;
        //System.out.println("=>Hello.sayHello方法调用返回结果: " + result);
    }
    
    public int invokeHadoopWrite(String url,String dir,String filename,List<String> proplist){
        int result =0;
        // XmlRpcClient
        XmlRpcClient client = new XmlRpcClient();
        // 客户端配置
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
                // 访问服务器路径、端口
            config.setServerURL(new URL(url));	//此10080端口正好被内嵌webServer监听；
            //config.setServerURL(new URL("http://localhost:10080"));
        } catch (MalformedURLException e) {
            log.error(e.toString()+" [url]:"+url+",[dir]:"+dir+",[filename]:"+filename+",[proplist]:"+proplist.toString());
            return result;
        }
        // 客户端设置
        client.setConfig(config);
        // 远程调用
        try {
            result = (int) client.execute("Hadoop.createCfg", new Object[] { dir,filename });
            result = (int) client.execute("Hadoop.writeCfg", new Object[] { dir,filename,proplist });
        } catch (XmlRpcException e) {
            log.error(e.toString()+" [url]:"+url+",[dir]:"+dir+",[filename]:"+filename+",[proplist]:"+proplist.toString());
        }
        return result;
        //System.out.println("=>Hello.sayHello方法调用返回结果: " + result);
    }
}
