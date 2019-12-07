package demo.shiro.realm;

import demo.shiro.realm.entity.Permission;
import demo.shiro.realm.entity.Role;
import demo.shiro.realm.entity.User;
import demo.shiro.realm.orm.JdbcTemplateUtils;
import demo.shiro.realm.service.PermissionService;
import demo.shiro.realm.service.RoleService;
import demo.shiro.realm.service.UserService;
import demo.shiro.realm.service.impl.PermissionServiceImpl;
import demo.shiro.realm.service.impl.RoleServiceImpl;
import demo.shiro.realm.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class BaseTest {

    protected PermissionService permissionService = new PermissionServiceImpl();
    protected RoleService roleService = new RoleServiceImpl();
    protected UserService userService = new UserServiceImpl();
//    @Autowired
////    protected PermissionServiceImpl permissionService;
//    protected PermissionService permissionService; // 注入接口与上面注入实现类效果一样，为什么？todo: 看源码，spring ioc依赖注入
//    @Autowired
//    protected RoleService roleService;
//    @Autowired
//    protected UserService userService;

    protected String password = "123";

    protected Permission p1;
    protected Permission p2;
    protected Permission p3;
    protected Role r1;
    protected Role r2;
    protected User u1;
    protected User u2;
    protected User u3;
    protected User u4;

    public void setUp() {
        JdbcTemplateUtils.jdbcTemplate().update("delete from sys_users");
        JdbcTemplateUtils.jdbcTemplate().update("delete from sys_roles");
        JdbcTemplateUtils.jdbcTemplate().update("delete from sys_permissions");
        JdbcTemplateUtils.jdbcTemplate().update("delete from sys_users_roles");
        JdbcTemplateUtils.jdbcTemplate().update("delete from sys_roles_permissions");


        //1、新增权限
        p1 = new Permission("user:create", "用户模块新增", Boolean.TRUE);
        p2 = new Permission("user:update", "用户模块修改", Boolean.TRUE);
        p3 = new Permission("menu:create", "菜单模块新增", Boolean.TRUE);
        permissionService.createPermission(p1);
        permissionService.createPermission(p2);
        permissionService.createPermission(p3);
        //2、新增角色
        r1 = new Role("admin", "管理员", Boolean.TRUE);
        r2 = new Role("user", "用户管理员", Boolean.TRUE);
        roleService.createRole(r1);
        roleService.createRole(r2);
        //3、关联角色-权限
        roleService.addPermissionsRelation(r1.getId(), p1.getId());
        roleService.addPermissionsRelation(r1.getId(), p2.getId());
        roleService.addPermissionsRelation(r1.getId(), p3.getId());

        roleService.addPermissionsRelation(r2.getId(), p1.getId());
        roleService.addPermissionsRelation(r2.getId(), p2.getId());

        //4、新增用户
        u1 = new User("zhang", password);
        u2 = new User("li", password);
        u3 = new User("wu", password);
        u4 = new User("wang", password);
        u4.setLocked(Boolean.TRUE);
        userService.createUser(u1);
        userService.createUser(u2);
        userService.createUser(u3);
        userService.createUser(u4);
        //5、关联用户-角色
        userService.addRolesRelation(u1.getId(), r1.getId());

    }

    public void login(String username, String password, String configFile) {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory(configFile);

        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            log.info("验证失败：" + e.toString());
        }
    }

    public Subject subject() {
        return SecurityUtils.getSubject();
    }

    public void tearDown() throws Exception {
        ThreadContext.unbindSubject();//退出时请解除绑定Subject到线程 否则对下次测试造成影响
    }
}
