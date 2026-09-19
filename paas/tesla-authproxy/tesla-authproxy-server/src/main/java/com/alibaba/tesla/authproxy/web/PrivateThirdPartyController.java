package com.alibaba.tesla.authproxy.web;

import com.alibaba.tesla.authproxy.web.common.PrivateBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 专有云 - 第三方系统服务 Controller
 *
 * @author yaoxing.gyx@alibaba-inc.com
 */
@Slf4j
@RestController
@RequestMapping("auth/private/thirdparty")
public class PrivateThirdPartyController extends PrivateBaseController {

}
