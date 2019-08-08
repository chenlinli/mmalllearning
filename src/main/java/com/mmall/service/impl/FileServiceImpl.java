package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.utils.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.multi.MultiFileChooserUI;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service("iFileService")
public class FileServiceImpl implements IFileService {


    /**
     * 上传文件
     * @param multipartFile
     * @param path
     * @return 上传文件的文件名
     */
    public String upload(MultipartFile multipartFile,String path){
        String fileName = multipartFile.getOriginalFilename();
        //扩展名 abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);//jpg
        //避免不同用户相同文件名
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        log.info("开始上传文件，上传文件名:{}，上传路径:{},新文件名：{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);//写权限
            fileDir.mkdirs();//可以创建多级墓库
        }
        //上传的文件
        File targetFile = new File(path,uploadFileName);
        try {
            multipartFile.transferTo(targetFile);
            //上次文件成功到tomcat服务器

            //targetfile上传到FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //上传完成后删除本地的 upload的对应文件
            targetFile.delete();//upload文件夹仍旧存在
        } catch (IOException e) {
            log.error("上传文件异常："+e);
        }

        return targetFile.getName();
    }
}
