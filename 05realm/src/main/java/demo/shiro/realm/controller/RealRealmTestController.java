package demo.shiro.realm.controller;

import demo.shiro.realm.BaseTest;
import junit.framework.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("realm")
public class RealRealmTestController extends BaseTest {

    @RequestMapping("testLoginSuccess")
    public void testLoginSuccess() {
        login("zhang", password,"classpath:shiro.ini");
        Assert.assertTrue(subject().isAuthenticated());
    }

    @RequestMapping("testLoginFailWithUnknownUsername")
//    @Test(expected = UnknownAccountException.class)
    public void testLoginFailWithUnknownUsername() {
        login("zhang1", password,"classpath:shiro.ini");
    }

    @RequestMapping("testLoginFailWithErrorPassowrd")
//    @Test(expected = IncorrectCredentialsException.class)
    public void testLoginFailWithErrorPassowrd() {
        login("zhang", password + "1","classpath:shiro.ini");
    }

    @RequestMapping("testLoginFailWithLocked")
//    @Test(expected = LockedAccountException.class)
    public void testLoginFailWithLocked() {
        login("wang", password + "1","classpath:shiro.ini");
    }

    @RequestMapping("testLoginFailWithLimitRetryCount")
//    @Test(expected = ExcessiveAttemptsException.class)
    public void testLoginFailWithLimitRetryCount() {
        for(int i = 1; i <= 5; i++) {
            try {
                login("wu", password + "1","classpath:shiro.ini");
            } catch (Exception e) {/*ignore*/}
        }
        login("wu", password + "1","classpath:shiro.ini");

        //需要清空缓存，否则后续的执行就会遇到问题(或者使用一个全新账户测试)
    }

    @RequestMapping("testHasRole")
    public void testHasRole() {
        login("zhang", password,"classpath:shiro.ini");
        Assert.assertTrue(subject().hasRole("admin"));
    }

    @RequestMapping("testNoRole")
    public void testNoRole() {
        login("li", password,"classpath:shiro.ini");
        Assert.assertFalse(subject().hasRole("admin"));
    }

    @RequestMapping("testHasPermission")
    public void testHasPermission() {
        login("zhang", password,"classpath:shiro.ini");
        Assert.assertTrue(subject().isPermittedAll("user:create", "menu:create"));
    }

    @RequestMapping("testNoPermission")
    public void testNoPermission() {
        login("li", password,"classpath:shiro.ini");
        Assert.assertFalse(subject().isPermitted("user:create"));
    }
}
