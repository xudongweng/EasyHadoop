/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyhadoopmonitor.controller.zoo;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

/**
 *
 * @author sheriff
 */
public class RPCZooKeeperController {
    private WebServer web_server;
    private Logger log=Logger.getLogger(RPCZooKeeperController.class);
    
    public RPCZooKeeperController(){
        try {
            this.web_server = new WebServer(10080);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
    
    public void startServer(){
        // XmlRpcServer服务
	XmlRpcServer xmlRpcServer = web_server.getXmlRpcServer();
        // 处理程序映射
	PropertyHandlerMapping phm = new PropertyHandlerMapping();
        try {
            // 为服务添加方法
            // ZooKeeper服务->XmlRpcHandler.class类
            phm.addHandler("ZooKeeper", XmlRpcZooKeeperHandler.class);
        } catch (XmlRpcException e) {
            log.error(e.toString());
        }
        xmlRpcServer.setHandlerMapping(phm);
        // 运行服务
        try {
            web_server.start();	
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

}
