package demo.shiro.realm.dao;

import demo.shiro.realm.entity.User;
import demo.shiro.realm.orm.JdbcTemplateUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Component
public class UserDaoImpl implements UserDao {

    private JdbcTemplate jdbcTemplate = JdbcTemplateUtils.jdbcTemplate();

    @Override
    public User createUser(User user) {
        final String sql = "insert into sys_users(username, password, salt, locked) values(?, ?, ?, ?)";
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[] { "id" });
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getSalt());
                preparedStatement.setBoolean(4, user.getLocked());
                return preparedStatement;
            }
        }, generatedKeyHolder);
        user.setId(generatedKeyHolder.getKey().longValue());
        return user;
    }

    @Override
    public void updateUser(User user) {
        String sql = "update sys_user set username=?,password=?,salt=?,locked=? where id=?";
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getSalt(), user.getLocked(), user.getId());
    }

    @Override
    public void addRolesRelation(Long userId, Long[] roleIds) {
        if(roleIds == null || roleIds.length == 0) {
            return;
        }
        String sql = "insert into sys_users_roles(user_id, role_id) values(?, ?)";
        for(Long roleId : roleIds) {
            if(!exists(userId, roleId)) {
                jdbcTemplate.update(sql, userId, roleId);
            }
        }
    }

    @Override
    public void removeRolesRelation(Long userId, Long[] roleIds) {
        if(roleIds == null || roleIds.length == 0) {
            return;
        }
        String sql = "delete from sys_users_roles where user_id=? and role_id=?";
        for(Long roleId : roleIds) {
            if(exists(userId, roleId)) {
                jdbcTemplate.update(sql, userId, roleId);
            }
        }
    }

    private boolean exists(Long userId, Long roleId) {
        String sql = "select count(1) from sys_users_roles where user_id=? and role_id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, roleId) != 0;
    }

    @Override
    public User findOne(Long userId) {
        String sql = "select id, username, password, salt, locked from sys_users where id=?";
        List<User> userList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(User.class), userId);
        if(userList.size() == 0) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    public User findByUsername(String username) {
        String sql = "select id, username, password, salt, locked from sys_users where username=?";
        List<User> userList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(User.class), username);
        if(userList.size() == 0) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    public Set<String> findRoles(String username) {
        String sql = "select role from sys_users u, sys_roles r,sys_users_roles ur where u.username=? and u.id=ur.user_id and r.id=ur.role_id";
        return new HashSet(jdbcTemplate.queryForList(sql, String.class, username));
    }

    @Override
    public Set<String> findPermissions(String username) {
        StringBuilder sql = new StringBuilder("select p.permission from sys_users u");
        sql.append(" left join sys_users_roles ur on u.id = ur.user_id");
        sql.append(" left join sys_roles_permissions rp on rp.role_id = ur.role_id");
        sql.append(" left join sys_permissions p on p.id = rp.permission_id");
        sql.append(" where u.username=?");
        return new HashSet(jdbcTemplate.queryForList(sql.toString(), String.class, username));
    }

    @Override
    public boolean checkUsernameIsExists(String username) {
        String sql = "select count(1) from sys_users where username=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, username) != 0;
    }
}
