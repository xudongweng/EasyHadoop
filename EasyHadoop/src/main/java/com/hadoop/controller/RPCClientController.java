/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author sheriff
 */
public class RPCClientController {
    private Logger log=Logger.getLogger(RPCClientController.class);
    public String invokeZooCreate(String url,String dir,String filename){
        String result = "";
        // XmlRpcClient
        XmlRpcClient client = new XmlRpcClient();
        // 客户端配置
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
                // 访问服务器路径、端口
            config.setServerURL(new URL(url));	//此10080端口正好被内嵌webServer监听；
            //config.setServerURL(new URL("http://localhost:10080"));
        } catch (MalformedURLException e) {
            log.error(e.toString());
            return result;
        }
        // 客户端设置
        client.setConfig(config);
        // 远程调用
        try {
            result = (String) client.execute("ZooKeeper.create", new Object[] { dir,filename });
        } catch (XmlRpcException e) {
            log.error(e.toString());
        }
        return result;
        //System.out.println("=>Hello.sayHello方法调用返回结果: " + result);
    }
}
