package demo.shiro.realm.service.impl;

import demo.shiro.realm.dao.RoleDao;
import demo.shiro.realm.dao.RoleDaoImpl;
import demo.shiro.realm.entity.Role;
import demo.shiro.realm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Service
public class RoleServiceImpl implements RoleService {

//    @Autowired
    private RoleDao roleDao = new RoleDaoImpl();

    @Override
    public Role createRole(Role role) {
        return roleDao.createRole(role);
    }

    @Override
    public Role updateRole(Role role) {
        return roleDao.updateRole(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        roleDao.deleteRole(roleId);
    }

    @Override
    public void addPermissionsRelation(Long roleId, Long... permissionIds) {
        roleDao.addPermissionsRelation(roleId, permissionIds);
    }

    @Override
    public void removePermissionsRelation(Long roleId, Long... permissionIds) {
        roleDao.removePermissionsRelation(roleId, permissionIds);
    }
}
