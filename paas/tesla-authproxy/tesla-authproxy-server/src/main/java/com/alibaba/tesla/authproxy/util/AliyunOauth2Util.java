package com.alibaba.tesla.authproxy.util;

import com.alibaba.tesla.authproxy.AuthProperties;
import com.alibaba.tesla.authproxy.lib.exceptions.AuthProxyException;
import com.alibaba.tesla.authproxy.model.vo.AliyunAccessTokenVO;
import com.alibaba.tesla.authproxy.model.vo.AliyunRefreshTokenVO;
import com.alibaba.tesla.authproxy.model.vo.AliyunUserInfoVO;
import com.alibaba.tesla.common.base.util.TeslaGsonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 阿里云 OAuth2 工具类
 *
 * @author yaoxing.gyx@alibaba-inc.com
 */
@Component
@Slf4j
public class AliyunOauth2Util {

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private AuthProperties authProperties;

    /**
     * 根据用户授权的 code 获取访问令牌
     *
     * @param code 用户授权 code
     * @return Aliyun Token
     */
    public AliyunAccessTokenVO getAccessToken(String code) {
        RequestBody formBody = new FormBody.Builder()
            .add("code", code)
            .add("client_id", authProperties.getOauth2ClientId())
            .add("redirect_uri", authProperties.getOauth2RedirectUri())
            .add("grant_type", "authorization_code")
            .add("client_secret", authProperties.getOauth2ClientSecret())
            .build();
        Request request = new Request.Builder().url(authProperties.getOauth2AccessTokenUri()).post(formBody).build();
        String response;
        try {
            response = HttpUtil.makeHttpCall(httpClient, request);
            log.info("Raw access token from aliyun, code={}, response={}", code, response);
        } catch (Exception e) {
            throw new AuthProxyException(String.format("Cannot get access token from aliyun, code=%s, exception=%s",
                code, ExceptionUtils.getStackTrace(e)));
        }
        try {
            return TeslaGsonUtil.gson.fromJson(response, AliyunAccessTokenVO.class);
        } catch (Exception e) {
            throw new AuthProxyException(String.format("Cannot parse access token from aliyun, code=%s, response=%s, "
                + "exception=%s", code, response, ExceptionUtils.getStackTrace(e)));
        }
    }

    /**
     * 根据用户的 refresh token 获取一个新的 access token
     *
     * @param refreshToken refresh token
     * @return 新的 access token
     */
    public AliyunRefreshTokenVO getRefreshToken(String refreshToken) {
        RequestBody formBody = new FormBody.Builder()
            .add("refresh_token", refreshToken)
            .add("client_id", authProperties.getOauth2ClientId())
            .add("grant_type", "refresh_token")
            .add("client_secret", authProperties.getOauth2ClientSecret())
            .build();
        Request request = new Request.Builder().url(authProperties.getOauth2AccessTokenUri()).post(formBody).build();
        String response;
        try {
            response = HttpUtil.makeHttpCall(httpClient, request);
            log.info("Raw refresh token from aliyun, refreshToken={}, response={}", refreshToken, response);
        } catch (Exception e) {
            throw new AuthProxyException(String.format("Cannot get refresh token from aliyun, refreshToken=%s, "
                + "exception=%s", refreshToken, ExceptionUtils.getStackTrace(e)));
        }
        try {
            return TeslaGsonUtil.gson.fromJson(response, AliyunRefreshTokenVO.class);
        } catch (Exception e) {
            throw new AuthProxyException(String.format("Cannot parse refresh token from aliyun, refreshToken=%s, "
                + "response=%s, exception=%s", refreshToken, response, ExceptionUtils.getStackTrace(e)));
        }
    }

    /**
     * 根据访问令牌获取当前用户的身份信息
     *
     * @param accessToken 访问令牌
     * @return 新的 access token
     */
    public AliyunUserInfoVO getUserInfo(String accessToken) {
        Request request = new Request.Builder().url(authProperties.getOauth2UserInfoUri())
            .header("Authorization", "Bearer " + accessToken)
            .get().build();
        String response;
        try {
            response = HttpUtil.makeHttpCall(httpClient, request);
            log.info("Raw user info from aliyun, accessToken={}, response={}", accessToken, response);
        } catch (Exception e) {
            throw new AuthProxyException(String.format("Cannot get user info from aliyun, accessToken=%s, "
                + "exception=%s", accessToken, ExceptionUtils.getStackTrace(e)));
        }
        try {
            return TeslaGsonUtil.gson.fromJson(response, AliyunUserInfoVO.class);
        } catch (Exception e) {
            throw new AuthProxyException(String.format("Cannot parse user info from aliyun, accessToken=%s, "
                + "response=%s, exception=%s", accessToken, response, ExceptionUtils.getStackTrace(e)));
        }
    }
}
