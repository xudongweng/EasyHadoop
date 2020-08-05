/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyhadoopmonitor.controller.hadoop;

import com.easyhadoopmonitor.controller.zoo.ZooKeeperConfigController;
import com.easyhadoopmonitor.helper.LinuxRunTimeHelper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author sheriff
 */
public class HadoopConfigController {
    private final Logger log=Logger.getLogger(ZooKeeperConfigController.class);
    private final LinuxRunTimeHelper runtime=new LinuxRunTimeHelper();
    
    public int createCfg(String filename) {
        File file=new File(filename);
        if (file.isFile()&&file.exists()){
            file.delete();
        }
        // 创建document
        Document doc = new Document();
        Element cfg = new Element("configuration");
        doc.setRootElement(cfg);
     
        return save(doc,filename);
    }
 
    public int save(Document doc,String xmlfilename) {
        // 将doc对象输出到文件
        try {
            // 创建xml文件输出流
            XMLOutputter xmlopt = new XMLOutputter();
            // 创建文件输出流
            FileWriter writer = new FileWriter(xmlfilename);
            // 指定文档格式
            Format fm = Format.getPrettyFormat();
            //fm.setEncoding("gb2312");
            //fm.setEncoding("utf8");
            fm.setEncoding("utf8");
            xmlopt.setFormat(fm);
            // 将doc写入到指定的文件中
            xmlopt.output(doc, writer);
            writer.close();
            return 1;
        } catch (IOException e) {
            log.error(e.toString());
            return 0;
        }
    }
    
    public int writeConfig(String filename,List<String> contestlist){
        SAXBuilder sb = new SAXBuilder();
        List<String> datadirList=new ArrayList<>();
        try {
            Document doc = sb.build(filename);
            Element root = doc.getRootElement();
            for(String contest:contestlist){
                Element elProperty=new Element("property");
                String[] newval=contest.split("=");                
                Element elName=new Element("name");
                Element elVal=new Element("val");
                elName.addContent(newval[0]);
                elVal.addContent(newval[1]);
                elProperty.addContent(elName);
                elProperty.addContent(elVal);
                root.addContent(elProperty);
                
                if(newval[0].contains(".dir")&&!newval[1].contains("qjournal://")){
                    if(newval[1].contains("file://"))
                        datadirList.add(newval[1].replace("file://", ""));
                    else
                        datadirList.add(newval[1]);
                }
                
            }
            save(doc,filename);
        }catch (JDOMException | IOException e) {
            log.error(e.toString());
            return 0;
        }
        log.info(filename+" was created.");
        for(String datadir : datadirList)
            log.info("Create directory "+datadir+" "+runtime.exec("mkdir -p "+datadir));

        return 1;
    }
}
