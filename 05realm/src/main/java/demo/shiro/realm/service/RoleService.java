package demo.shiro.realm.service;

import demo.shiro.realm.entity.Role;

public interface RoleService {

    /**
     * 创建角色
     * @param role
     * @return
     */
    public Role createRole(Role role);

    /**
     * 更新角色
     * @param role
     * @return
     */
    public Role updateRole(Role role);

    /**
     * 删除角色
     * @param roleId
     * @return
     */
    public void deleteRole(Long roleId);

    /**
     * 添加角色-权限之间关系
     * @param roleId
     * @param permissionIds
     */
    public void addPermissionsRelation(Long roleId, Long... permissionIds);

    /**
     * 移除角色-权限之间关系
     * @param roleId
     * @param permissionIds
     */
    public void removePermissionsRelation(Long roleId, Long... permissionIds);
}
