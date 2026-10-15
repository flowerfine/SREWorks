package com.alibaba.tesla.productops.controllers;

import com.alibaba.tesla.action.common.TeslaBaseResult;
import com.alibaba.tesla.action.controller.BaseController;
import com.alibaba.tesla.productops.services.NodeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jinghua.yjh
 */
@Slf4j
@RestController
@RequestMapping
public class RuntimeController extends BaseController {

    @Autowired
    NodeService nodeService;

    @GetMapping("/frontend/nodes/archives")
    public TeslaBaseResult archives(String stageId) {
        log.debug("stageId: {}", stageId);
        String appId = getBizAppId().split(",")[0];
        String nodeId = appId + "|app|T:";
        return buildSucceedResult(nodeService.tree(nodeId, appId, stageId));
    }
}
