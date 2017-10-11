package com.news.service;

import com.alibaba.fastjson.JSONObject;
import com.news.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Baoxu on 2017/8/28.
 */
@Service
public class QiniuService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(QiniuService.class);

    private static String QINIU_IMAGE_DOMAIN = "http://ove2poozl.bkt.clouddn.com";

    //设置好账号的ACCESS_KEY和SECRET_KEY
    private static String ACCESS_KEY = "tupzSqqdtiyZKsgIFFtOCWVwuBpkOvNT82tI7Yhq";
    private static String SECRET_KEY = "BrI1gKLiAtruhSkO9EFQbwe1jOiB7bYxV7oS4Ois";
    //要上传的空间
    private static String bucketname = "toutiao-bucket";

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

    Zone z = Zone.autoZone();
    Configuration c = new Configuration(z);

    //创建上传对象
    UploadManager uploadManager = new UploadManager(c);

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        return auth.uploadToken(bucketname);
    }

    public String save(MultipartFile file) throws IOException {
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos+1).toLowerCase();
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {
            return null;
        }
        String fileName = UUID.randomUUID().toString().replaceAll("-","")+"."+fileExt;
        try {
            //调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());

            //打印返回的信息
            System.out.println(res.bodyString());
            return  QINIU_IMAGE_DOMAIN + "/"+JSONObject.parseObject(res.bodyString()).get("key");

            //if (res.isOK()&&res.isJson()) {
            //    return  QINIU_IMAGE_DOMAIN + "/"+JSONObject.parseObject(res.bodyString()).get("key");
            //} else {
            //    logger.error("七牛异常："+res.bodyString());
            //    return null;
            //}
        } catch (QiniuException e) {
          logger.error("七牛异常："+e.getMessage());
          return null;
        }
    }

}
