package demo.shiro.realm.dao;

import demo.shiro.realm.entity.Role;
import demo.shiro.realm.orm.JdbcTemplateUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//@Component
public class RoleDaoImpl implements RoleDao {

    private JdbcTemplate jdbcTemplate = JdbcTemplateUtils.jdbcTemplate();

    @Override
    public Role createRole(Role role) {
        final String sql = "insert into sys_roles(role, description, available) values(?,?,?)";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement psst = connection.prepareStatement(sql, new String[] { "id" });
                psst.setString(1, role.getRole());
                psst.setString(2, role.getDescription());
                psst.setBoolean(3, role.getAvailable());
                return psst;
            }
        }, keyHolder);
        role.setId(keyHolder.getKey().longValue());

        return role;
    }

    @Override
    public Role updateRole(Role role) {
        String sql = "update sys_roles set role=?,description=?,salt=?,available=? where id=?";
        jdbcTemplate.update(sql, role.getRole(), role.getAvailable(), role.getAvailable(), role.getId());
        return role;
    }

    @Override
    public void deleteRole(Long roleId) {
        //首先把和role关联的相关表数据删掉
        String sql = "delete from sys_users_roles where role_id=?";
        jdbcTemplate.update(sql, roleId);

        sql = "delete from sys_roles where id=?";
        jdbcTemplate.update(sql, roleId);
    }

    @Override
    public void addPermissionsRelation(Long roleId, Long... permissionIds) {
        if(permissionIds == null || permissionIds.length == 0) {
            return;
        }
        String sql = "insert into sys_roles_permissions(role_id, permission_id) values(?,?)";
        for(Long permissionId : permissionIds) {
            if(!exists(roleId, permissionId)) {
                jdbcTemplate.update(sql, roleId, permissionId);
            }
        }
    }

    @Override
    public void removePermissionsRelation(Long roleId, Long... permissionIds) {
        if(permissionIds == null || permissionIds.length == 0) {
            return;
        }
        String sql = "delete from sys_roles_permissions where role_id=? and permission_id=?";
        for(Long permissionId : permissionIds) {
            if(exists(roleId, permissionId)) {
                jdbcTemplate.update(sql, roleId, permissionId);
            }
        }
    }

    private boolean exists(Long roleId, Long permissionId) {
        String sql = "select count(1) from sys_roles_permissions where role_id=? and permission_id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, roleId, permissionId) != 0;
    }
}
