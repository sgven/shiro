package demo.shiro.realm.service.impl;

import demo.shiro.realm.dao.UserDao;
import demo.shiro.realm.dao.UserDaoImpl;
import demo.shiro.realm.entity.User;
import demo.shiro.realm.service.UserService;
import demo.shiro.realm.utils.PasswordHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

//@Service
public class UserServiceImpl implements UserService {

//    @Autowired
    private UserDao userDao = new UserDaoImpl();
//    @Autowired
    private PasswordHelper passwordHelper = new PasswordHelper();

    @Override
    public User createUser(User user) {
        // 校验用户名是否重复
        boolean exists = userDao.checkUsernameIsExists(user.getUsername());
        if (exists) {
            throw new RuntimeException("用户名已存在");
        }
        // 加密密码
        passwordHelper.encryptPassword(user);
        return userDao.createUser(user);
    }

    @Override
    public void changePassword(Long userId, String newPassword) {
        User user = userDao.findOne(userId);
        user.setPassword(newPassword);
        passwordHelper.encryptPassword(user);
        userDao.updateUser(user);
    }

    @Override
    public void addRolesRelation(Long userId, Long... roleIds) {
        userDao.addRolesRelation(userId, roleIds);
    }

    @Override
    public void removeRolesRelation(Long userId, Long... roleIds) {
        userDao.removeRolesRelation(userId, roleIds);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public Set<String> findRoles(String username) {
        return userDao.findRoles(username);
    }

    @Override
    public Set<String> findPermissions(String username) {
        return userDao.findPermissions(username);
    }
}
