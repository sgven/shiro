package demo.shiro.realm.dao;

import demo.shiro.realm.entity.Permission;

public interface PermissionDao {
    Permission createPermission(Permission permission);

    void deletePermission(Long permissionId);
}
