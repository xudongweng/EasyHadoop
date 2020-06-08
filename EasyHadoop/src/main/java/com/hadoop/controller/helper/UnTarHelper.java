package com.hadoop.controller.helper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sheriff
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
 
public class UnTarHelper {
    private File sourceFile;
    private File targetFile;
    private TarArchiveInputStream  TarArchiveInputStream;
    private static final int BUFFER = 4096;
    /** 
    * 文件 解归档 
    *  
    * @param srcPath 源文件路径 
    * @param destPath 目标文件路径 
    * @throws Exception 
    */  
    public UnTarHelper(String sourceFilePath, String targetFilePath) throws FileNotFoundException {
        sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
        throw new FileNotFoundException("file can not be found:"+ sourceFilePath);
        }
        if (sourceFile.isDirectory()) {
        throw new IllegalArgumentException("file can not be directory:"+ sourceFilePath);
        }
        targetFile = new File(targetFilePath);
        if (targetFile.isFile()) {
        throw new IllegalArgumentException("file should be directory:"+ targetFilePath);
        }
    }

    public void  unTar() throws IOException{
        try {
            TarArchiveInputStream = new TarArchiveInputStream(new FileInputStream(sourceFile));
            //buildDerectory(targetFile);
            TarArchiveEntry entry = null;  
            while ((entry = TarArchiveInputStream.getNextTarEntry()) != null) {
                String dir = targetFile.getPath() + File.separator + entry.getName();
                File dirFile = new File(dir);
                // 文件检查
                fileProber(dirFile);
                if (entry.isDirectory()) {
                    dirFile.mkdirs();
                } else {
                    dearchiveFile(dirFile, TarArchiveInputStream);
                }
            }
        } finally {
            if (TarArchiveInputStream != null) {
                TarArchiveInputStream.close();
            }
        }
    }
    /** 
    * 文件解归档 
    *  
    * @param destFile 目标文件 
    * @param tais TarArchiveInputStream 
    * @throws Exception 
    */
    private static void dearchiveFile(File destFile, TarArchiveInputStream tais)throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
        int count;
        byte data[] = new byte[BUFFER];
        while ((count = tais.read(data, 0, BUFFER)) != -1) {
            bos.write(data, 0, count);
        }
        bos.close();
    }
    
    private static void fileProber(File dirFile) {
        File parentFile = dirFile.getParentFile();
        if (!parentFile.exists()) {
            // 递归寻找上级目录
            fileProber(parentFile);
            parentFile.mkdir();
        }
     }
}
