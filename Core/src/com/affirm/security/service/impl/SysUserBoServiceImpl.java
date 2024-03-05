package com.affirm.security.service.impl;

import com.affirm.common.dao.SysUserDAO;
import com.affirm.security.model.SysUser;
import com.affirm.security.service.SysUserBoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("sysUserBoService")
public class SysUserBoServiceImpl implements SysUserBoService {

    @Autowired
    SysUserDAO sysUserDAO;

    @Override
    public SysUser getSysUserById(Integer sysUserId) {
        return sysUserDAO.getSysUserById(sysUserId);
    }

}
