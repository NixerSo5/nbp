package com.nixer.nprox.service.auth.impl;

import com.alibaba.fastjson.JSONObject;
import com.ning.http.util.UTF8UrlEncoder;
import com.nixer.nprox.dao.*;
import com.nixer.nprox.entity.common.*;
import com.nixer.nprox.entity.common.dto.*;
import com.nixer.nprox.exception.CustomException;
import com.nixer.nprox.service.auth.AuthService;
import com.nixer.nprox.tools.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: JoeTao
 * createAt: 2018/9/17
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtTokenUtil;
    private final AuthDao authMapper;


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserInfoDao userInfoDao;


    @Autowired
    private SysLoginTypeDao sysLoginTypeDao;



    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, @Qualifier("CustomUserDetailsService") UserDetailsService userDetailsService, JwtUtils jwtTokenUtil, AuthDao authMapper) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authMapper = authMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetail register(UserDetail userDetail, int type) {//1???????????????  2????????????
        final String username = userDetail.getUsername();
        SysLoginType findSysLoginType = sysLoginTypeDao.findByLoginName(username);
        if (findSysLoginType != null) {
            throw new CustomException(ResultJson.failure(ResultCode.BAD_REQUEST, "???????????????"));
        }
        //??????????????????????????????
        String useruid = UUID.randomUUID().toString();
        userDetail.setUsername(useruid);
        authMapper.insert(userDetail);
        if (userDetail.getId() <= 0) {
            throw new CustomException(ResultJson.failure(ResultCode.SERVER_ERROR, "??????????????????"));
        }

        SysLoginType sysLoginType = new SysLoginType();
        sysLoginType.setLogin_type(type);
        sysLoginType.setLogin_name(username);
        sysLoginType.setUserid((int) userDetail.getId());
        sysLoginType.setSys_username(useruid);
        sysLoginTypeDao.insert(sysLoginType);
        if (sysLoginType.getId() <= 0) {
            throw new CustomException(ResultJson.failure(ResultCode.SERVER_ERROR, "??????????????????"));
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUserid((int) userDetail.getId());
        if (type == 1) {
            userInfo.setPhone(username);
        } else {
            userInfo.setEmail(username);
        }
        userInfo.setLastip(userDetail.getLastip());
        userInfo.setImgurl("https://picsum.photos/80/80");
        userInfoDao.insert(userInfo);
        //TODO ??????charges  Agent???????????????20%  User100%
        long roleId = userDetail.getRole().getId();
        Role role = authMapper.findRoleById(roleId);
        userDetail.setRole(role);
        authMapper.insertRole(userDetail.getId(), roleId);
        return userDetail;
    }

    @Override
    public ResponseUserToken login(String username, String password) {
        //????????????
        final Authentication authentication = authenticate(username, password);
        //??????????????????
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //??????token
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        final String token = jwtTokenUtil.generateAccessToken(userDetail);
        //??????token
        jwtTokenUtil.putToken(username, token);
        return new ResponseUserToken(token, userDetail);

    }

    @Override
    public void logout(String token) {
        if (token != null) {
            token = token.substring(tokenHead.length());
            String userName = jwtTokenUtil.getUsernameFromToken(token);
            jwtTokenUtil.deleteToken(userName);
        }
    }

    @Override
    public ResponseUserToken refresh(String oldToken) {
        String token = oldToken.substring(tokenHead.length());
        String username = jwtTokenUtil.getUsernameFromToken(token);
        UserDetail userDetail = (UserDetail) userDetailsService.loadUserByUsername(username);
        if (jwtTokenUtil.canTokenBeRefreshed(token, userDetail.getLastPasswordResetDate())) {
            token = jwtTokenUtil.refreshToken(token);
            jwtTokenUtil.putToken(username, token);
            return new ResponseUserToken(token, userDetail);
        }
        return null;
    }

    @Override
    public UserDetail getUserByToken(String token) {
        token = token.substring(tokenHead.length());
        return jwtTokenUtil.getUserFromToken(token);
    }

    @Override
    public ResultJson sendVerificationCode(SendVerificationCodeDto codeDto) throws IOException {
        //?????????????????????key ??????????????????
        String value = redisUtil.get("SENDSMSLOCK:" + codeDto.getSend());
        if (value == null) {
            String token = randomCode();
            String msgx = "???????????????????????????" + token + "???????????????5??????????????????????????????????????????";
            msgx = UTF8UrlEncoder.encodePath(msgx);
            String xurl = "http://api.sms654.com/smsUTF8.aspx?type=send&username=Hudex&" +
                    "password=E10ADC3949BA59ABBE56E057F20F883E&gwid=45e5195c" +
                    "&mobile=" + codeDto.getSend() + "&message=" + msgx + "&rece=json";
            String req = HttpUtil.doPost(xurl);
            if (req != null && req != "") {
                JSONObject jso = JSONObject.parseObject(req);
                if (jso.getString("code").equals("0")) {
                    redisUtil.set("SENDSMSLOCK:" + codeDto.getSend(), "lock", 60L);
                    redisUtil.set("SENDSMS:" + codeDto.getSend(), token, 5 * 60L);
                    return ResultJson.ok();
                }
                if (jso.getString("code").equals("-33")) {
                    return ResultJson.failure(ResultCode.BAD_REQUEST, "??????????????????");
                } else {
                    return ResultJson.failure(ResultCode.SERVER_ERROR);
                }
            } else {
                return ResultJson.failure(ResultCode.SERVER_ERROR);
            }
        } else {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "????????????!");
        }
    }


    public static void main(String[] args) {
//        String token =  randomCode();
//        String msgx = "???????????????????????????"+token+"???????????????5??????????????????????????????????????????";
//        msgx = UTF8UrlEncoder.encodePath(msgx);
//        String xurl = "http://api.sms654.com/smsUTF8.aspx?type=send&username=Hudex&" +
//                "password=E10ADC3949BA59ABBE56E057F20F883E&gwid=45e5195c" +
//                "&mobile="+"17461905219"+"&message="+msgx+"&rece=json";
//        String req = HttpUtil.doPost(xurl);
//        System.out.println(req);

        UserDetail userDetail = new UserDetail(1l, "1", null, "1");
        userDetail.setUsername("sss");
        String f = userDetail.getUsername();
        userDetail.setUsername("sssf");
        System.out.println(f);


    }


    @Override
    public ResultJson sendEmailVerificationCode(SendVerificationCodeDto codeDto) {
        String token = randomCode();
        try {
            MailUtil.sendEmail(codeDto.getSend(), token);
            redisUtil.set("SENDEMAIL:" + codeDto.getSend(), token, 5 * 60L);
            return ResultJson.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.failure(ResultCode.SERVER_ERROR);
        }
    }

    @Override
    public ResponseUserToken login(SuperLoginDto superLoginDto) {
        //????????????
        final Authentication authentication = authenticate(superLoginDto.getUsername(), superLoginDto.getPassword());
        //??????????????????
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //??????token
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        final String token = jwtTokenUtil.generateAccessToken(userDetail);
        //??????token
        jwtTokenUtil.putToken(userDetail.getUsername(), token);
        superLoginDto.setId((int) userDetail.getId());
        //????????????ip???????????????
        userInfoDao.updateLastIpAndTime(superLoginDto);
        return new ResponseUserToken(token, userDetail);
    }

    @Override
    public ResultJson modifyPassword(ModifyPasswordDto modifyPasswordDto, String ipaddress) {
        if (UserUnlock(modifyPasswordDto.getUserid(), ipaddress)) {
            Buser buser = authMapper.findBuserById(modifyPasswordDto.getUserid());
//        if(buser.getPhone()==null||buser.getPassword()==""){
//            return ResultJson.failure(ResultCode.BAD_REQUEST,"???????????????????????????!");
//        }
            return modifyPsw(buser, modifyPasswordDto, 0);
        } else {
            return ResultJson.failure(ResultCode.SERVER_ERROR, "????????????????????????");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public ResultJson modifyPsw(Buser buser, ModifyPasswordDto modifyPasswordDto, int type) {
        //String vcode = redisUtil.get("SENDSMS:"+buser.getPhone());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (type == 0) {
            if (!encoder.matches(modifyPasswordDto.getOldPsw(), buser.getPassword())) {
                return ResultJson.failure(ResultCode.BAD_REQUEST, "????????????!");
            }
        }
        //if(vcode.equals(modifyPasswordDto.getSmscode())){
        //??????
        modifyPasswordDto.setNewPsw(encoder.encode(modifyPasswordDto.getNewPsw()));
        authMapper.updatePassword(modifyPasswordDto);
        this.logout(modifyPasswordDto.getToken());
        return ResultJson.ok();
//        }else{
//            return ResultJson.failure(ResultCode.BAD_REQUEST,"???????????????!");
//        }
    }

    @Override
    public ResultJson findPassword(FindPassWordDto findPassWordDto, String ipaddress) {
        SysLoginType sysLoginType = sysLoginTypeDao.findByLoginName(findPassWordDto.getUsername());
        if (sysLoginType == null) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "???????????????");
        }
        UserDetail userDetail = authMapper.findById(sysLoginType.getUserid());
        UserInfo userInfo = userInfoDao.findByUserid(sysLoginType.getUserid());
        if (userDetail == null || userInfo == null) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "???????????????");
        }

        String vcode = "";
        if (findPassWordDto.getFindtype() == 1) {
            vcode = redisUtil.get("SENDSMS:" + userInfo.getPhone());
        } else {
            vcode = redisUtil.get("SENDEMAIL:" + userInfo.getEmail());
        }
        if (!vcode.equals(findPassWordDto.getVerifycode())) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "???????????????");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        ModifyPasswordDto modifyPasswordDto = new ModifyPasswordDto();
        modifyPasswordDto.setUserid(sysLoginType.getUserid().longValue());
        modifyPasswordDto.setNewPsw(encoder.encode(findPassWordDto.getNew_password()));
        authMapper.updatePassword(modifyPasswordDto);
        this.logout(modifyPasswordDto.getToken());
        return ResultJson.ok();

    }

    @Override
    public ResultJson userVerify(UserDetail userDetail, UserVerifyDto singlePramDto, String ipaddress) throws IOException {
        long userid = userDetail.getId();
        UserInfo userInfo = userInfoDao.findByUserid(userDetail.getId());
        if (userInfo == null) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "???????????????!");
        }
        if ((singlePramDto.getUnlockType().equals("0") && StringUtils.isEmpty(userInfo.getPhone())) || (singlePramDto.getUnlockType().equals(
                "1") && StringUtils.isEmpty(userInfo.getEmail()))) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, "???????????????!");
        }
        String value = "";
        if (singlePramDto.getUnlockType().equals("0")) {
            value = redisUtil.get("SENDSMS:" + userInfo.getPhone());
        } else {
            value = redisUtil.get("SENDEMAIL:" + userInfo.getEmail());
        }
        if (singlePramDto.getVrifyCode().equals(value)) {
            redisUtil.set("USERUNLOCKVERIFY:USERID:" + userid + "_" + ipaddress, "unlock", 60L * 5);
            return ResultJson.ok();
        }
        return ResultJson.failure(ResultCode.BAD_REQUEST, "???????????????????????????");
    }



    @Override
    public UserDetail getSysUserByUserId(long id) {
        return authMapper.findById(id);
    }


    public Boolean UserUnlock(long userid, String ipaddress) {
        String key = redisUtil.get("USERUNLOCKVERIFY:USERID:" + userid + "_" + ipaddress);
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return true;
    }


    private Authentication authenticate(String username, String password) {
        try {
            //?????????????????????userDetailsService.loadUserByUsername()??????????????????????????????????????????????????????????????????????????????security ??? context??????
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException | BadCredentialsException e) {
            throw new CustomException(ResultJson.failure(ResultCode.LOGIN_ERROR, e.getMessage()));
        }
    }


    public static String randomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
}
