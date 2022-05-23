package com.zxc.community.controller;

import com.zxc.community.annotation.LoginRequired;
import com.zxc.community.entity.User;
import com.zxc.community.service.FollowService;
import com.zxc.community.service.LikeService;
import com.zxc.community.service.UserService;
import com.zxc.community.util.CommunityConstant;
import com.zxc.community.util.CommunityUtil;
import com.zxc.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class) ;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "还没有选择图片!");
            return "/site/setting";
        }

        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));

        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确!");
            return "/site/setting";
        }

        // 生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;

        //确定文件存放路径
        File dest = new File(uploadPath + "/" + filename);

        try {
            // store the file
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败 服务器异常", e);
        }

        // 更新当前用户头像 web访问路径
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // head url
        fileName = uploadPath + "/" + fileName;
        // suffix
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        // response picture
        response.setContentType("image/"+suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);  //从硬盘输入
                ServletOutputStream os = response.getOutputStream(); //自动关闭资源 从网页输出
                ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取图像失败： " + e.getMessage());
        }
    }


    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(Model model, String prePassword, String curPassword, String checkPassword) {
        if(prePassword == null) {
            model.addAttribute("prePasswordErr","请输入原始密码!");
            return "/site/setting";
        }

        User user = hostHolder.getUser();
        if(!user.getPassword().equals(CommunityUtil.md5(prePassword + user.getSalt()))){
            model.addAttribute("prePasswordErr","原密码不正确，请再次输入!");
            return "/site/setting";
        }

        if (curPassword == null) {
            model.addAttribute("curPasswordErr", "请输入新密码!");
            return "/site/setting";
        }

        if (curPassword.equals(prePassword)) {
            model.addAttribute("curPasswordErr", "输入密码和原密码相同!");
            return "/site/setting";
        }

        if (!curPassword.equals(checkPassword)) {
            model.addAttribute("checkPasswordErr", "确认密码不正确!");
            return "/site/setting";
        }

        String newPassword = CommunityUtil.md5(curPassword + user.getSalt());
        userService.updatePassword(user.getId(), newPassword);

        return "redirect:/index";
    }

    @RequestMapping(value = "/profile/{userId}", method = RequestMethod.GET)
    public String findUserLikeCount(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        int userLikeCount = likeService.findUserLikeCount(userId);

        model.addAttribute("user", user);
        model.addAttribute("likeCount",userLikeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.findHasFollowEntity(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }

        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }
}
