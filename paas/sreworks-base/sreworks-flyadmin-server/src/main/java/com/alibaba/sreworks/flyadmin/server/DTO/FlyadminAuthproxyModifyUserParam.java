package com.alibaba.sreworks.flyadmin.server.DTO;

import lombok.Data;

@Data
public class FlyadminAuthproxyModifyUserParam {

    String nickName;

    String loginName;

    String password;

    String email;

    String phone;

    String avatar;

}
