package demo.shiro.permission.controller;

import demo.shiro.permission.BaseTest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * 基于角色的访问控制（即隐式角色，基于角色的隐式访问控制）
 * Shiro 提供的 checkRole/checkRoles 和 hasRole/hasAllRoles 不同的地方是
 * checkRole/checkRoles 它在判断为假的情况下会抛出 UnauthorizedException 异常。
 */
@RestController
@RequestMapping("role")
public class RoleTestController extends BaseTest {

    @RequestMapping(value = "hasRole")
    public void hasRole(@RequestParam String username, @RequestParam String password) {
        testHasRole(username, password);
    }

    @RequestMapping(value = "checkRole")
    public void checkRole(@RequestParam String username, @RequestParam String password) {
        testCheckRole(username, password);
    }

    private void testHasRole(String username, String password) {
        login(username, password, "classpath:shiro-role.ini");
        //判断拥有角色：role1
        Assert.isTrue(subject().hasRole("role1"), "该用户没有角色role1，无...权限");
        //判断拥有角色：role1 and role2
        Assert.isTrue(subject().hasAllRoles(Arrays.asList("role1", "role2")), "该用户没有角色role1、role2，无...权限");
        //判断拥有角色：role1 and role2 and !role3
        boolean[] result = subject().hasRoles(Arrays.asList("role1", "role2", "role3"));
        Assert.isTrue(result[0], "该用户没有角色role1，无...权限");
        Assert.isTrue(result[1], "该用户没有角色role1、role2，无...权限");
        Assert.isTrue(result[2], "该用户没有角色role3，无...权限");
    }

    private void testCheckRole(String username, String password) {
        login(username, password, "classpath:shiro-role.ini");
        //断言拥有角色：role1
        subject().checkRole("role1");
        //断言拥有角色：role1 and role3 失败抛出异常
        subject().checkRoles("role1", "role3");
    }
}
