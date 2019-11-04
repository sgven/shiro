package demo.shiro.permission.controller;

import demo.shiro.permission.BaseTest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基于资源的访问控制（显示角色）
 * 这种方式的一般规则是“资源标识符：操作”，即是资源级别的粒度
 *
 * 规则：“用户名=密码，角色 1，角色 2”“角色=权限 1，权限 2”，即首先根据用户名找到角色，然后根据角色再找到权限；即角色是权限集合
 * 需要维护“用户——角色，角色——权限（资源：操作）”之间的关系
 *
 * role1=user:create,user:update    or      role2=user:update,delete
 * “:”表示资源/操作/实例的分割；“,”表示操作的分割；“*”表示任意资源/操作/实例
 * role3=system:user:*（推荐这种写法）      or      role3=system:user
 * 表示角色 3 拥有 system:user 的所有权限
 *
 * 注意，通过“system:user:*”验证“system:user:create,delete,update:view”可以，但是反过来是不成立的
 *
 * role4=*:view 表示用户拥有所有资源的“view”权限
 * 假设判断的权限是“"system:user:view”，那么需要“role4=::view”这样写才行
 *
 * Shiro 对权限字符串缺失部分的处理：
 * *可以匹配所有，不加*可以进行前缀匹配；
 * 但是如“*:view”不能匹配“system:user:view”，需要使用“*:*:view”，即后缀匹配必须指定前缀（多个冒号就需要多个*来匹配）
 */
@RestController
@RequestMapping("permission")
public class PermissionTestController extends BaseTest {

    @RequestMapping("isPermitted")
    public void IsPermitted(@RequestParam String username, @RequestParam String password) {
        testIsPermitted(username, password);
    }

    @RequestMapping("checkPermission")
    public void checkPermission(@RequestParam String username, @RequestParam String password) {
        testCheckPermission(username, password);
    }

    private void testIsPermitted(String username, String password) {
        login(username, password, "classpath:shiro-permission.ini");
        //判断拥有权限：user:create
        Assert.isTrue(subject().isPermitted("user:create"), "用户没有新增权限");
        //判断拥有权限：user:update and user:delete
        Assert.isTrue(subject().isPermittedAll("user:update", "user:delete"), "用户没有修改、删除权限");
        //判断没有权限：user:view
        Assert.isTrue(subject().isPermitted("user:view"), "用户没有查看权限");
    }

    private void testCheckPermission(String username, String password) {
        login(username, password, "classpath:shiro-permission.ini");
        //断言拥有权限：user:create
        subject().checkPermission("user:create");
        //断言拥有权限：user:delete and user:update
        subject().checkPermissions("user:delete", "user:update");
        //断言拥有权限：user:view 失败抛出异常
        subject().checkPermissions("user:view");
    }

}
