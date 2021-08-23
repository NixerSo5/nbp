package com.nixer.nprox.controller.api;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ning.http.util.Base64;
import com.nixer.nprox.tools.Kaptcha;
import com.nixer.nprox.tools.RedisUtil;
import com.nixer.nprox.tools.ResultCode;
import com.nixer.nprox.tools.ResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KaptchaController {

    @Autowired
    private RedisUtil redisUtil;


    @GetMapping("/defaultKaptcha")
    public ResultJson defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws Exception {
      //  byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        Map outmap = new HashMap();
        try {
            // 代码方式创建:DefaultKaptcha
            Kaptcha single = Kaptcha.getInstance();
            DefaultKaptcha defaultKaptcha = single.produce();
            // 生产验证码字符串并保存到session中
            String createText = defaultKaptcha.createText();
            //前后端分离
            //httpServletRequest.getSession().setAttribute("vrifycode", createText);


            BufferedImage challenge = defaultKaptcha.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
            String imageStr= Base64.encode(jpegOutputStream.toByteArray());
            outmap.put("img","data:image/jpg;base64,"+imageStr );
            //生成验证码对应的token  以token为key  验证码为value存在redis中
            String codeToken = UUID.randomUUID().toString();
            redisUtil.set("VRIFYCODE:"+codeToken,createText,60L*3);
            outmap.put("token", codeToken);
            // 使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return ResultJson.failure(ResultCode.SERVER_ERROR);
        }
        return ResultJson.ok(outmap);
//
//        // 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
//        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
//        httpServletResponse.setHeader("Cache-Control", "no-store");
//        httpServletResponse.setHeader("Pragma", "no-cache");
//        httpServletResponse.setDateHeader("Expires", 0);
//        httpServletResponse.setContentType("image/jpeg");
//        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
//        responseOutputStream.write(captchaChallengeAsJpeg);
//        responseOutputStream.flush();
//        responseOutputStream.close();
    }

}
