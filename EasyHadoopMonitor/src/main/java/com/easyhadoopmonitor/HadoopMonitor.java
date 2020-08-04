/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyhadoopmonitor;

import com.easyhadoopmonitor.controller.hadoop.RPCHadoopController;

/**
 *
 * @author sheriff
 */
public class HadoopMonitor {
    public static void main(String[] args) {
        RPCHadoopController rpczkc=new RPCHadoopController();
        rpczkc.startServer();
    }
}
