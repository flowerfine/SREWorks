package com.alibaba.tesla.authproxy;

import com.alibaba.tesla.authproxy.exceptions.TeslaJwtException;
import com.alibaba.tesla.authproxy.interceptor.*;
import com.alibaba.tesla.authproxy.model.UserDO;
import com.alibaba.tesla.authproxy.service.AuthPolicy;
import com.alibaba.tesla.authproxy.service.TeslaUserService;
import com.alibaba.tesla.authproxy.util.AuthUtil;
import com.alibaba.tesla.authproxy.util.CookieUtil;
import com.alibaba.tesla.authproxy.util.TeslaJwtUtil;
import com.alibaba.tesla.authproxy.web.common.PrivateResultBuilder;
import com.alibaba.tesla.common.utils.TeslaResult;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller基类，处理返回值，获取登录用户，注入所有Controller公共依赖类
 */
@Slf4j
@Controller
public class BaseController {

    @Autowired
    public AuthPolicy authPolicy;

    @Autowired
    private AuthProperties authProperties;

    @Autowired
    private TeslaUserService teslaUserService;

    @Autowired
    private AuthUtil authUtil;

    /**
     * 获取登录用户信息
     */
    public UserDO getLoginUser(HttpServletRequest request) {
        LoginInterceptor loginIterceptor = authPolicy.getLoginServiceManager();
        Cookie loginUserIdCookie;

        if (loginIterceptor instanceof DataBaseLoginInterceptor) {
            //兼容请求头中放用户信息
            UserDO headerUserDo = authUtil.getExtAppUser(request);
            if (null != headerUserDo) {
                return headerUserDo;
            }

            //兼容网关自带app
            String userName = request.getHeader("X-Auth-User");
            String empId = request.getHeader("X-Empid");
            if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(empId)) {
                return teslaUserService.getUserByLoginName(userName);
            }

            String loginUserName = "";
            loginUserIdCookie = CookieUtil.getCookie(request, Constants.COOKIE_DATABASE_LOGIN_USER_ID);
            log.info("DataBaseLoginInterceptor validate cookie {}={}", Constants.COOKIE_DATABASE_LOGIN_USER_ID,
                loginUserIdCookie);
            if (null != loginUserIdCookie && !StringUtils.isEmpty(loginUserIdCookie.getValue())) {
                loginUserName = loginUserIdCookie.getValue();
            } else {
                Cookie userToken = CookieUtil.getCookie(request, Constants.COOKIE_DATABASE_LOGIN_TOKEN);
                if (null == userToken || StringUtils.isEmpty(userToken.getValue())) {
                    throw new ApplicationException(TeslaResult.NOAUTH, "Auth failure, Please login.");
                }
                Claims claims = null;
                try {
                    claims = TeslaJwtUtil.verify(userToken.getValue(), authProperties.getOauth2JwtSecret());
                } catch (TeslaJwtException e) {
                    throw new ApplicationException(TeslaResult.NOAUTH, "Tesla jwt token verify failure, Please login.");
                }
                loginUserName = String.valueOf(claims.get(Constants.COOKIE_DATABASE_LOGIN_USER_ID));
            }
            UserDO userDo = teslaUserService.getUserByLoginName(loginUserName);
            if (null == userDo) {
                log.error("Auth failure Get user from database return null. cookie = {}.", loginUserName);
                throw new ApplicationException(TeslaResult.NOAUTH, "Auth failure, Please login.");
            }
            return userDo;
        } else {
            throw new ApplicationException(TeslaResult.FAILURE, "login.policyClass is error");
        }
    }

    /**
     * 构造分页信息
     *
     * @param pageSize
     * @param currentPageNum
     * @return
     */
    public PageBounds buildPageBounds(Integer pageSize, Integer currentPageNum, Boolean isContainsTotalCount) {
        if (null == pageSize) {
            pageSize = 10;
        }
        if (null == currentPageNum) {
            currentPageNum = 1;
        }
        if (null == isContainsTotalCount) {
            isContainsTotalCount = true;
        }
        PageBounds pageBounds = new PageBounds();
        pageBounds.setPage(currentPageNum);
        pageBounds.setLimit(pageSize);
        pageBounds.setContainsTotalCount(isContainsTotalCount);
        return pageBounds;
    }

    /**
     * 构造分页数据集
     *
     * @param pageSize
     * @param currentPageNum
     * @param ret
     * @return
     */
    public Map<String, Object> buildResult(Integer pageSize, Integer currentPageNum, PageList<?> ret) {

        if (null == pageSize) {
            pageSize = 10;
        }
        if (null == currentPageNum) {
            currentPageNum = 1;
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 200);
        result.put("rows", ret);
        if (null == ret || null == ret.getPaginator()) {
            result.put("total", 0);
        } else {
            result.put("total", ret.getPaginator().getTotalCount());
        }
        return result;
    }

    public Map<String, Object> buildResult(Object data) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 200);
        result.put("data", data);
        result.put("message", "success");
        return result;
    }

    public Map<String, Object> buildResult(String code, Object data) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", code);
        result.put("data", data);
        result.put("message", "success");
        return result;
    }

    public Map<String, Object> buildResult(String code, Object data, String message) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", code);
        result.put("data", data);
        result.put("message", message);
        return result;
    }

    public Map<String, Object> buildResult() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 200);
        result.put("data", null);
        result.put("message", "success");
        return result;
    }

    public Map<String, Object> buildErrorResult(String code, String errorMsg) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", code);
        result.put("data", null);
        result.put("message", errorMsg);
        return result;
    }

    public Map<String, Object> buildErrorResult(int code, String errorMsg) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", code);
        result.put("data", null);
        result.put("message", errorMsg);
        return result;
    }

    public Map<String, Object> buildErrorResult(String errorMsg) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 500);
        result.put("data", null);
        result.put("message", errorMsg);
        return result;
    }

    public Map<String, Object> buildSuccessResult(String msg) {
        Map<String, Object> result = new HashMap<>(8);
        result.put("code", 200);
        result.put("data", null);
        result.put("message", msg);
        return result;
    }

    public TeslaResult buildSuccessTeslaResult(Object data) {
        TeslaResult result = new TeslaResult();
        result.setCode(TeslaResult.SUCCESS);
        result.setData(data);
        result.setMessage("SUCCESS");
        return result;
    }

    public TeslaResult buildFailedTeslaResult(String message) {
        TeslaResult result = new TeslaResult();
        result.setCode(TeslaResult.BAD_REQUEST);
        result.setData(null);
        result.setMessage(message);
        return result;
    }

    public TeslaResult buildValidationResult(BindingResult result) {
        Map<String, String> errorMap = new HashMap<>(10);
        List<FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError error : fieldErrors) {
            errorMap.put(error.getField(), error.getDefaultMessage());
        }
        return PrivateResultBuilder.buildExtValidationErrorResult(errorMap);
    }

}
