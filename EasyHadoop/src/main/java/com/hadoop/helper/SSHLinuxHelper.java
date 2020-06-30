/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadoop.helper;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author sheriff
 */
public class SSHLinuxHelper {
    private JSch jsch;
    private Session session;
    private final Logger log=Logger.getLogger(SSHLinuxHelper.class);
   
    
    public String execCmd(String host,String user,String password,String command){
        StringBuilder sb=new StringBuilder();
        try {
            //1、创建JSch类，好比是FlashFXP工具
            jsch = new JSch();
            //2、创建本次的文件传输会话对象，并连接到SFTP服务器。它好比是通过FlashFXP工具连接到SFTP服务器
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            
            channel.connect();
            BufferedReader reader;
            try{
                InputStream in = channel.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                String buf;
                while ((buf = reader.readLine()) != null) {
                    //System.out.println(buf);
                    sb.append(buf);
                }
                reader.close();
            }catch(IOException e){
                log.error(e.toString());
            }
            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            log.error(e.toString());
        }
        return sb.toString();
    }
    
    public String execCmd(String host,String user,String password,int port,String command){
        StringBuilder sb=new StringBuilder();
        try {
            //1、创建JSch类，好比是FlashFXP工具
            jsch = new JSch();
            //2、创建本次的文件传输会话对象，并连接到SFTP服务器。它好比是通过FlashFXP工具连接到SFTP服务器
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            
            channel.connect();
            BufferedReader reader = null;
            
            try{
                InputStream in = channel.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                
                String buf = null;
                while ((buf = reader.readLine()) != null) {
                    //System.out.println(buf);
                    sb.append(buf);
                }
                reader.close();
                
            }catch(IOException e){
                log.error(e.toString());
            }
            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            log.error(e.toString());
        }
        return sb.toString();
    }
    
    public void filelist(String host,String user,String password,String path){
        try {
            //1、创建JSch类，好比是FlashFXP工具
            jsch = new JSch();
            //2、创建本次的文件传输会话对象，并连接到SFTP服务器。它好比是通过FlashFXP工具连接到SFTP服务器
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            //3、在该session会话中开启一个SFTP通道，之后就可以在该通道中进行文件传输了
            ChannelSftp channelSftp = (ChannelSftp)session.openChannel("sftp");
            
            channelSftp.connect();
            try {
                //channelSftp.setFilenameEncoding("gbk");
                Vector vector  = channelSftp.ls(path);
                for(Object obj :vector){
                    if(obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry){
                        String fileName = ((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getFilename();
                        System.out.println(fileName);
                    }
                }
            } catch (SftpException e) {
                log.error(e.toString());
            }
            channelSftp.disconnect();
            session.disconnect();
        }catch (JSchException e) {
            log.error(e.toString());
        }
    }
    
    public void uploadfile(String host,String user,String password,String sFile,String dst){
        try {
            //1、创建JSch类，好比是FlashFXP工具
            jsch = new JSch();
            //2、创建本次的文件传输会话对象，并连接到SFTP服务器。它好比是通过FlashFXP工具连接到SFTP服务器
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            //3、在该session会话中开启一个SFTP通道，之后就可以在该通道中进行文件传输了
            ChannelSftp channelSftp = (ChannelSftp)session.openChannel("sftp");
            channelSftp.connect();
            try {
                //4、进行文件传输操作：put()、get()....
                channelSftp.put(sFile, dst);
            } catch (SftpException e) {
                System.out.println(e.toString());
            }
            //5、操作完毕后，关闭通道并退出本次会话
            if(channelSftp!=null && channelSftp.isConnected()){
                channelSftp.disconnect();
            }
            if(session!=null && session.isConnected()){
                session.disconnect();
            }
        }catch (JSchException e) {
            log.error(e.toString());
        }
    }
}
