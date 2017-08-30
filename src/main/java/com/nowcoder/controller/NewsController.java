package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.QiniuService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 资讯相关操作入口
 * Created by Baoxu on 2017/8/28.
 */
@Controller
public class NewsController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;
    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        System.out.println("file:"+file);
        String fileUrl = null;
        try {
          fileUrl = qiniuService.save(file);
          System.out.println("fileUrl:"+fileUrl);
        } catch (IOException e) {
            logger.error("上传图片失败" + e.getMessage());
        } finally {
            return fileUrl;
        }
    }

    /**
     * 资讯详情展示
     * @param newsId
     * @param model
     * @return
     */
    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId")int newsId, Model model){
        News news = newsService.getNews(newsId);
        if (news != null) {
            List<Comment> comments = commentService.getCommentsById(news.getId(), EntityType.ENTITY_NEWS);
            List<ViewObject> commentVOS = new ArrayList<ViewObject>();
            for (Comment comment : comments) {
                ViewObject commentVO = new ViewObject();
                commentVO.set("comment",comment);
                commentVO.set("user", userService.getUser(comment.getUserId()));
                commentVOS.add(commentVO);
            }
            model.addAttribute("comments",commentVOS);
            model.addAttribute("news",news);
            model.addAttribute("owner",userService.getUser(news.getUserId()));
        }
        return "detail";
    }

    @RequestMapping(path = {"/image"}, method = {RequestMethod.GET})
    public void getImage(@RequestParam("name") String imageName,
                           HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR+imageName)),
                    response.getOutputStream());
        } catch (IOException e) {
            logger.error("图片读取错误"+e.getMessage());
        }
    }

    /**
     * 添加资讯
     * 将上传的图片保存到七牛服务器，
     * 并将图片返回的链接连同资讯标题，链接保存到数据库中
     * TODO:目前图片上传大小限制没有处理，一旦上传文件稍大，就会抛异常
     * @param file
     * @param title
     * @param link
     * @return
     */
    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})
    public String addNews(@RequestParam("file") MultipartFile file,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {
            //上传到七牛服务器，并返回图片链接
            String image = qiniuService.save(file);
            if (image == null) {
                return "share";
            }
            News news = new News();
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setImage(image);
            news.setLink(link);
            if (hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                news.setUserId(3);
            }
            newsService.addNews(news);
            return "redirect:/";
        } catch (Exception e) {
            logger.error("添加资讯失败" + e.getMessage());
            return "share";
        }
    }

    /**
     * 增加评论，并更新评论数
     * @param newsId
     * @param content
     * @return
     */
    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId, @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setUserId(hostHolder.getUser().getId());
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);
        //    更新评论数
            int count = commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            newsService.updateCommentCount(count,comment.getEntityId());
        } catch (Exception e) {
            logger.error("提交评论错误"+e.getMessage());
        }
        return "redirect:/news/"+String.valueOf(newsId);
    }

    /**
     * 定向到分享页面
     * @return
     */
    @RequestMapping(path = {"/shareNews/"}, method = {RequestMethod.GET})
    public String shareNews(){
        return "share";
    }
}
