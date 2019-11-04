package demo.shiro.permission.controller;

import demo.shiro.permission.BaseTest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("authorizer")
public class AuthorizerTestController extends BaseTest {

    @RequestMapping("isPermitted")
    public void isPermitted(@RequestParam String username, @RequestParam String password) {
        login(username, password, "classpath:shiro-authorizer.ini");
        // 判断拥有权限：user:create
        Assert.isTrue(subject().isPermitted("user1:create"), "用户1没有新增权限");
        Assert.isTrue(subject().isPermitted("user2:create"), "用户2没有新增权限");
        // 通过二进制位的方式表示权限
        Assert.isTrue(subject().isPermitted("+user1+2"), "用户1没有修改权限"); // 修改权限
        Assert.isTrue(subject().isPermitted("+user1+8"), "用户1没有查看权限"); // 查看权限
        Assert.isTrue(subject().isPermitted("+user2+10"), "用户2没有修改、查看权限"); // 修改、查看权限
        Assert.isTrue(subject().isPermitted("+user1+4"), "用户1没有删除权限"); // 删除权限
        // 通过MyRolePermissionResolver解析得到的权限
        Assert.isTrue(subject().isPermitted("menu:view"), "用户没有查看权限");
    }

    @RequestMapping("isPermitted2")
    public void isPermitted2(@RequestParam String username, @RequestParam String password) {
        login(username, password, "classpath:shiro-jdbc-authorizer.ini");
        // 判断拥有权限：user:create
        Assert.isTrue(subject().isPermitted("user1:create"), "用户1没有新增权限");
        Assert.isTrue(subject().isPermitted("user2:create"), "用户2没有新增权限");
        // 通过二进制位的方式表示权限
        Assert.isTrue(subject().isPermitted("+user1+2"), "用户1没有修改权限"); // 修改权限
        Assert.isTrue(subject().isPermitted("+user1+8"), "用户1没有查看权限"); // 查看权限
        Assert.isTrue(subject().isPermitted("+user2+10"), "用户2没有修改、查看权限"); // 修改、查看权限
        Assert.isTrue(subject().isPermitted("+user1+4"), "用户1没有删除权限"); // 删除权限
        // 通过MyRolePermissionResolver解析得到的权限
        Assert.isTrue(subject().isPermitted("menu:view"), "用户没有查看权限");
    }
}
