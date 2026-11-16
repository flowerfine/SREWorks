package com.alibaba.tesla.authproxy.util;

import com.alibaba.tesla.authproxy.lib.exceptions.AuthProxyErrorCode;
import com.alibaba.tesla.authproxy.lib.exceptions.AuthProxyException;
import com.alibaba.tesla.authproxy.model.UserDO;
import org.springframework.util.StringUtils;

/**
 * 用户工具
 *
 * @author yaoxing.gyx@alibaba-inc.com
 */
public class UserUtil {

    public static final String USER_ID_PREFIX_EMPID = "empid::";

    public static final String USER_ID_PREFIX_ALIYUNPK = "aliyunpk::";

    public static final String USER_ID_PREFIX_PUBLIC = "public::";

    public static final String USER_ID_UNKNOWN = "unknown";

    public static final String USER_ID_PREFIX_DEPID = "depid::";

    /**
     * 根据传入的 user 对象构造 user id 字符串
     *
     * @param user 用户对象
     * @return 唯一标识符
     */
    public static String getUserId(UserDO user) {
        if (!StringUtils.isEmpty(user.getEmpId())) {
            return USER_ID_PREFIX_EMPID + user.getEmpId();
        } else if (!StringUtils.isEmpty(user.getAliyunPk())) {
            return USER_ID_PREFIX_ALIYUNPK + user.getAliyunPk();
        } else if (!StringUtils.isEmpty(user.getLoginName())) {
            return USER_ID_PREFIX_PUBLIC + user.getLoginName();
        } else {
            return USER_ID_UNKNOWN;
        }
    }
}
