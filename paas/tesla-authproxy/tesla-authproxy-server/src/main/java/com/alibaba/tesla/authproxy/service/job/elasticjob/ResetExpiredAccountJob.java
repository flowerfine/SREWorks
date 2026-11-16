package com.alibaba.tesla.authproxy.service.job.elasticjob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ElasticJob 定时调度 - 对已经过期的密码进行强制重置操作
 *
 * @author yaoxing.gyx@alibaba-inc.com
 */
@Slf4j
@Component
public class ResetExpiredAccountJob implements SimpleJob {

    @Override
    public void execute(ShardingContext context) {

    }
}
