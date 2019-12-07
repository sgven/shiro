package demo.shiro.realm.service;

import demo.shiro.realm.entity.Permission;

public interface PermissionService {

    public Permission createPermission(Permission permission);
    public void deletePermission(Long permissionId);

}
