package demo.shiro.realm.dao;

import demo.shiro.realm.entity.User;

import java.util.Set;

public interface UserDao {

    User createUser(User user);

    void updateUser(User user);

    void addRolesRelation(Long userId, Long... roleIds);

    void removeRolesRelation(Long userId, Long... roleIds);

    User findOne(Long userId);

    User findByUsername(String username);

    Set<String> findRoles(String username);

    Set<String> findPermissions(String username);

    boolean checkUsernameIsExists(String username);
}
