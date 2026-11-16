package com.alibaba.tesla.authproxy.service.impl;

import com.alibaba.tesla.authproxy.ApplicationException;
import com.alibaba.tesla.authproxy.model.mapper.AppMapper;
import com.alibaba.tesla.authproxy.model.mapper.RoleMenuRelMapper;
import com.alibaba.tesla.authproxy.model.mapper.MenuMapper;
import com.alibaba.tesla.authproxy.model.AppDO;
import com.alibaba.tesla.authproxy.model.RoleMenuRelDO;
import com.alibaba.tesla.authproxy.model.MenuDO;
import com.alibaba.tesla.authproxy.model.vo.MenuDoTreeVO;
import com.alibaba.tesla.authproxy.service.TeslaMenuService;
import com.alibaba.tesla.common.utils.TeslaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>Title: TeslaMenuServiceImpl.java<／p>
 * <p>Description: 菜单服务实现 <／p>
 * <p>Copyright: Copyright (c) 2017<／p>
 * <p>Company: alibaba <／p>
 *
 * @author tandong.td@alibaba-inc.com
 * @version 1.0
 * @date 2017年5月3日
 */
@Service
@Slf4j
public class TeslaMenuServiceImpl implements TeslaMenuService {

    /**
     * 菜单信息数据访问接口
     */
    @Autowired
    MenuMapper menuMapper;

    @Autowired
    RoleMenuRelMapper roleMenuRelMapper;

    @Autowired
    AppMapper appMapper;

    /**
     * 初始化应用下的菜单
     *
     * @param appId  应用ID
     * @param userId 用户ID
     * @param menus  菜单数据
     * @return
     * @throws ApplicationException
     */
    @Override
    @Transactional
    @Deprecated
    public void initMenuByApp(String appId, long userId, List<MenuDO> menus) throws ApplicationException {

        //批量删除appId下现有菜单和用户的关系
        int umr = menuMapper.deleteUserMenusByAppId(appId);

        //批量删除appId下现有菜单数据
        int m = menuMapper.deleteByAppId(appId);

        //批量添加现有菜单
        int im = menuMapper.batchInsert(menus);

        log.info("删除用户菜单关系[{}],删除菜单[{}],添加菜单[{}]", umr, m, im);
    }

    @Override
    public int insert(MenuDO record, String appId) throws ApplicationException {
        return 0;
    }

    @Override
    public int batchInsert(List<MenuDO> menus) throws ApplicationException {
        try {
            return menuMapper.batchInsert(menus);
        } catch (Exception e) {
            log.error("批量插入菜单失败", e);
            throw new ApplicationException(TeslaResult.FAILURE, "error.menu.batchInsert");
        }
    }

    @Override
    public Map<String, Object> syncMenus(List<MenuDO> menus) throws ApplicationException {
        for (MenuDO menu : menus) {
            MenuDO m = menuMapper.getMenuByCode(menu.getMenuCode());
            if (null != m) {
                m.setMenuName(menu.getMenuName());
                m.setIsEnable(menu.getIsEnable());
                m.setMenuTitle(menu.getMenuTitle());
                m.setMenuUrl(menu.getMenuUrl());
                m.setParentCode(menu.getParentCode());
                m.setIcon(menu.getIcon());
            }
            menuMapper.updateByPrimaryKey(m);
        }
        return null;
    }
}