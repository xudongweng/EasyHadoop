/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyhadoopmonitor;

import com.easyhadoopmonitor.controller.xmlrpc.RPCZooKeeperController;

/**
 *
 * @author sheriff
 */
public class ZooKeeperMonitor {
    public static void main(String[] args) {
        RPCZooKeeperController rpczkc=new RPCZooKeeperController();
        rpczkc.startServer();
    }
}
