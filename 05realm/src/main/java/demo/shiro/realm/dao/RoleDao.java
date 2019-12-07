package demo.shiro.realm.dao;

import demo.shiro.realm.entity.Role;

public interface RoleDao {
    Role createRole(Role role);

    Role updateRole(Role role);

    void deleteRole(Long roleId);

    void addPermissionsRelation(Long roleId, Long... permissionIds);

    void removePermissionsRelation(Long roleId, Long... permissionIds);
}
