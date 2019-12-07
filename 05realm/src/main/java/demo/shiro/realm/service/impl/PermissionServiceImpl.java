package demo.shiro.realm.service.impl;

import demo.shiro.realm.dao.PermissionDao;
import demo.shiro.realm.dao.PermissionDaoImpl;
import demo.shiro.realm.entity.Permission;
import demo.shiro.realm.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Service
public class PermissionServiceImpl implements PermissionService {

//    @Autowired
    private PermissionDao permissionDao = new PermissionDaoImpl();

    @Override
    public Permission createPermission(Permission permission) {
        return permissionDao.createPermission(permission);
    }

    @Override
    public void deletePermission(Long permissionId) {
        permissionDao.deletePermission(permissionId);
    }
}
