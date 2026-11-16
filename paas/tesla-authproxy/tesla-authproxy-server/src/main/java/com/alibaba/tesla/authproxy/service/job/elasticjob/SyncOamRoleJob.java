package com.alibaba.tesla.authproxy.service.job.elasticjob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.springframework.stereotype.Component;

/**
 * ElasticJob 定时调度 - 同步 roles 表数据到 OAM 角色中
 *
 * @author yaoxing.gyx@alibaba-inc.com
 */
@Component
public class SyncOamRoleJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
    }
}
