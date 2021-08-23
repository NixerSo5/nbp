package com.nixer.nprox.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.nixer.nprox.entity.common.ResponseUserToken;
import com.nixer.nprox.entity.common.Role;
import com.nixer.nprox.entity.common.UserDetail;
import com.nixer.nprox.entity.common.dto.LoginDto;
import com.nixer.nprox.entity.common.dto.SendVerificationCodeDto;
import com.nixer.nprox.entity.common.dto.SinglePramDto;
import com.nixer.nprox.entity.common.dto.FindPassWordDto;
import com.nixer.nprox.entity.common.SuperLoginDto;
import com.nixer.nprox.service.auth.AuthService;
import com.nixer.nprox.tools.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author JoeTao
 * createAt: 2018/9/17
 */

@RestController
@Api("登陆注册及刷新token")
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.header}")
    private String tokenHeader;

    private final AuthService authService;

    @Autowired
    private RedisUtil redisUtil;


    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login")
    @ApiOperation(value = "登陆", notes = "登陆成功返回token,登陆之前请先注册账号")
    public ResultJson<ResponseUserToken> login(HttpServletRequest request, @RequestBody LoginDto user) {
        if (user.getVrifycode() == null) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "验证码不存在");
        } else {
            boolean b = true;
            //TODO 验证码没做
           // boolean b = vrifyKatpcha(request, user.getVrifycode());
            SuperLoginDto superLoginDto = new SuperLoginDto();
            superLoginDto = JSONObject.parseObject(JSONObject.toJSONString(user),SuperLoginDto.class);
            if (b) {
                String lastip = GetIpUtil.getUserIp(request);
                superLoginDto.setLastip(lastip);
                final ResponseUserToken response = authService.login(superLoginDto);
                //TODO 冻结需要重新制作目前没有踢出
                UserDetail userDetail = authService.getSysUserByUserId(response.getUserDetail().getId());
                if(userDetail.getFreeze()==1){
                    return ResultJson.failure(ResultCode.BAD_REQUEST,"用户已被冻结");
                }
                return ResultJson.ok(response);
            } else {
                return ResultJson.failure(ResultCode.BAD_REQUEST, "验证码错误");
            }
        }
    }


    @GetMapping(value = "/logout")
    @ApiOperation(value = "登出", notes = "退出登陆")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header")})
    public ResultJson logout(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        if (token == null) {
            return ResultJson.failure(ResultCode.UNAUTHORIZED);
        }
        authService.logout(token);
        return ResultJson.ok();
    }


    @GetMapping(value = "/vrifyGraphicalCode")
    @ApiOperation(value = "校验图形验证码 doid为验证码", notes = "")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header")})
    public ResultJson vrifyGraphicalCode(HttpServletRequest request, @RequestBody SinglePramDto singlePramDto) {
        String token = request.getHeader(tokenHeader);
        if (token == null) {
            return ResultJson.failure(ResultCode.UNAUTHORIZED);
        }
        boolean b = vrifyKatpcha(request,singlePramDto.getDoid());
        if(b){
            return ResultJson.ok();
        }
        return ResultJson.failure(ResultCode.BAD_REQUEST,"验证码错误");
    }

//    @GetMapping(value = "/user")
//    @ApiOperation(value = "根据token获取用户信息", notes = "根据token获取用户信息")
//    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header")})
//    public ResultJson getUser(HttpServletRequest request) {
//        String token = request.getHeader(tokenHeader);
//        if (token == null) {
//            return ResultJson.failure(ResultCode.UNAUTHORIZED);
//        }
//        UserDetail userDetail = authService.getUserByToken(token);
//        return ResultJson.ok(userDetail);
//    }


    @PostMapping("/findPassword")
    @ApiOperation(value = "找回密码")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Authorization token",defaultValue = "Bearer ", required = true, dataType = "string", paramType = "header")})
    public ResultJson findPassword(HttpServletRequest request,@RequestBody FindPassWordDto findPassWordDto){
        String ipaddr =  GetIpUtil.getUserIp(request);
        return authService.findPassword(findPassWordDto,ipaddr);
    }


    @PostMapping(value = "/phoneSign")
    @ApiOperation(value = "手机用户注册")
    public ResultJson phoneSign(HttpServletRequest request, @RequestBody LoginDto user) {
        if (user.getVrifycode() == null) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "验证码不存在");
        } else {

            boolean b = vrifyKatpcha(request, user.getVrifycode());
            b = true;
            if (b) {
                if (StringUtils.isAnyBlank(user.getUsername(), user.getPassword())) {
                    return ResultJson.failure(ResultCode.BAD_REQUEST);
                }
                String value = redisUtil.get("SENDSMS:" + user.getUsername());
                if (value != null && value.equals(user.getCode())) {
                    UserDetail userDetail = new UserDetail(user.getUsername(), user.getPassword(), Role.builder().id(1l).build());
                    String lastip=GetIpUtil.getUserIp(request);
                    userDetail.setLastip(lastip);
                    return ResultJson.ok(authService.register(userDetail, 1));
                } else {
                    return ResultJson.failure(ResultCode.BAD_REQUEST, "手机验证码错误");
                }
            } else {
                return ResultJson.failure(ResultCode.BAD_REQUEST, "图形验证码错误");
            }
        }
    }

    @PostMapping(value = "/emailSign")
    @ApiOperation(value = "邮箱用户注册")
    public ResultJson emailSign(HttpServletRequest request, @RequestBody LoginDto user) {
        if (user.getVrifycode() == null) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "验证码不存在");
        } else {
            boolean b = vrifyKatpcha(request, user.getVrifycode());
            b = true;
            if (b) {
                if (StringUtils.isAnyBlank(user.getUsername(), user.getPassword())) {
                    return ResultJson.failure(ResultCode.BAD_REQUEST);
                }
                String value = redisUtil.get("SENDEMAIL:" + user.getUsername());
                if (value != null && value.equals(user.getCode())) {
                    UserDetail userDetail = new UserDetail(user.getUsername(), user.getPassword(), Role.builder().id(1l).build());
                    return ResultJson.ok(authService.register(userDetail,2));
                } else {
                    return ResultJson.failure(ResultCode.BAD_REQUEST, "验证码错误");
                }
            } else {
                return ResultJson.failure(ResultCode.BAD_REQUEST, "验证码错误");
            }
        }
    }

    @PostMapping(value = "/sendSmsVerificationCode")
    @ApiOperation(value = "手机验证码发送")
    public ResultJson sendSmsVerificationCode(HttpServletRequest request, @RequestBody SendVerificationCodeDto codeDto) throws IOException {
        if (codeDto.getVrifycode() == null) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "验证码不存在");
        } else {
            boolean b = vrifyKatpcha(request, codeDto.getVrifycode());
            if (b) {
                return  authService.sendVerificationCode(codeDto);
            } else {
                return ResultJson.failure(ResultCode.BAD_REQUEST, "验证码错误");
            }
        }
    }


    @PostMapping(value = "/sendEmailVerificationCode")
    @ApiOperation(value = "邮箱验证码发送")
    public ResultJson sendEmailVerificationCode(HttpServletRequest request, @RequestBody SendVerificationCodeDto codeDto) throws IOException {
        if (codeDto.getVrifycode() == null) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "验证码不存在");
        } else {
            boolean b = vrifyKatpcha(request, codeDto.getVrifycode());
            if (b) {
                return  authService.sendEmailVerificationCode(codeDto);
            } else {
                return ResultJson.failure(ResultCode.BAD_REQUEST, "验证码错误");
            }
        }
    }




    @GetMapping(value = "refresh")
    @ApiOperation(value = "刷新token")
    public ResultJson refreshAndGetAuthenticationToken(
            HttpServletRequest request){
        String token = request.getHeader(tokenHeader);
        ResponseUserToken response = authService.refresh(token);
        if(response == null) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "token无效");
        } else {
            return ResultJson.ok(response);
        }
    }


    public Boolean vrifyKatpcha(HttpServletRequest req,String vrifycode){
        String token = req.getHeader(tokenHeader);
       // String expect = (String) req.getSession().getAttribute("vrifycode");
        String expect = redisUtil.get("VRIFYCODE:"+token.split(" ")[1]);
        if (expect==null||(expect != null && !expect.equalsIgnoreCase(vrifycode))) {
        redisUtil.remove("VRIFYCODE:"+token.split(" ")[1]);
        //req.getSession().removeAttribute("vrifycode");
        return false;
        }
        redisUtil.remove("VRIFYCODE:"+token.split(" ")[1]);
        return true;
        }
}
