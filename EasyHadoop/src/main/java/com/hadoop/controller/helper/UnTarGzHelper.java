package com.hadoop.controller.helper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author sheriff
 */
public class UnTarGzHelper {

 
    public static void main(String[] args) {
        deCompressGZipFile("D:\\software\\zookeeper-3.4.14.tar.gz", "D:\\software\\zookeeper");
    }
 
    /**
     * Tar文件解压方法
     *
     * @param tarGzFile 要解压的压缩文件名称（绝对路径名称）
     * @param destDir   解压后文件放置的路径名（绝对路径名称）当路径不存在，会自动创建
     * @return 解压出的文件列表
     */
    public static void deCompressGZipFile(String tarGzFile, String destDir) {
 
        // 建立输出流，用于将从压缩文件中读出的文件流写入到磁盘
        TarArchiveEntry entry = null;
        TarArchiveEntry[] subEntries = null;
        File subEntryFile = null;
        try (FileInputStream fis = new FileInputStream(tarGzFile);
             GZIPInputStream gis = new GZIPInputStream(fis);
             TarArchiveInputStream taris = new TarArchiveInputStream(gis);) {
            while ((entry = taris.getNextTarEntry()) != null) {
                StringBuilder entryFileName = new StringBuilder();
                entryFileName.append(destDir).append(File.separator).append(entry.getName());
                File entryFile = new File(entryFileName.toString());
                if (entry.isDirectory()) {
                    if (!entryFile.exists()) {
                        entryFile.mkdir();
                    }
                    subEntries = entry.getDirectoryEntries();
                    for (int i = 0; i < subEntries.length; i++) {
                        try (OutputStream out = new FileOutputStream(subEntryFile)) {
                            subEntryFile = new File(entryFileName + File.separator + subEntries[i].getName());
                            IOUtils.copy(taris, out);
                        } catch (Exception e) {
e.printStackTrace();
                        }
                    }
                } else {
                    checkFileExists(entryFile);
                    OutputStream out = new FileOutputStream(entryFile);
                    IOUtils.copy(taris, out);
                    out.close();
                    //如果是gz文件进行递归解压
                    if (entryFile.getName().endsWith(".gz")) {
                        deCompressGZipFile(entryFile.getPath(), destDir);
                    }
                }
            }
            //如果需要刪除之前解压的gz文件，在这里进行
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public static void checkFileExists(File file) {
        //判断是否是目录
        if (file.isDirectory()) {
            if (!file.exists()) {
                file.mkdir();
            }
        } else {
            //判断父目录是否存在，如果不存在，则创建
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
