/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author sheriff
 */
public class RPCClientController {
    public void invokeZoo(Map<String,String> zoomap){
        String result = "";

        // XmlRpcClient
        XmlRpcClient client = new XmlRpcClient();

        // 客户端配置
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
                // 访问服务器路径、端口
                config.setServerURL(new URL("http://localhost:10080"));	//此10080端口正好被内嵌webServer监听；
        } catch (MalformedURLException e) {
                e.printStackTrace();
        }
        // 客户端设置
        client.setConfig(config);


        // 远程调用
        try {
            result = (String) client.execute("ZooKeeper.write", new Object[] { zoomap });
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        System.out.println("=>Hello.sayHello方法调用返回结果: " + result);
    }
}
