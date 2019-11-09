package demo.shiro.authentication.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RestController(value = "home") // 这里value的值并不会映射到@RequestMapping注解中
@RestController
@RequestMapping(value = "home")
@Slf4j
public class LoginTestController {

//    @RequestMapping(value = "/") // 与下面的写法，两者并不是一样的，上面这个访问路径要多一个 “/”
    @RequestMapping()
    public String index() {
        return "hello";
    }

//    @RequestMapping(value = "/login") // 这两者访问路径一样，后面还有路径的，前面的 “/” 可省略
    @RequestMapping(value = "login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return testLoginDemo(username, password);
    }

    @RequestMapping(value = "login2")
    public String login2(@RequestParam String username, @RequestParam String password) {
        return testCustomRealm(username, password);
    }

    @RequestMapping(value = "login3")
    public String login3(@RequestParam String username, @RequestParam String password) {
        return testCustomMultiRealm(username, password);
    }

    @RequestMapping(value = "login4")
    public String login4(@RequestParam String username, @RequestParam String password) {
        return testJdbcRealm(username, password);
    }

    /**
     * 登录，仅简单测试，有两个问题需要完善：
     * 1.用户名 / 密码硬编码在 ini 配置文件，以后需要改成如数据库存储，且密码需要加密存储；
     * 2.用户身份 Token 可能不仅仅是用户名 / 密码，也可能还有其他的，如登录时允许用户名 / 邮箱 / 手机号同时登录。
     * @param username
     * @param password
     * @return
     */
    private String testLoginDemo(String username, String password) {
        return login(username, password, "classpath:shiro.ini");
    }

    /**
     * 自定义单个Realm，Realm即安全数据源 (shiro从Realm获取安全数据,然后校验)
     * @param username
     * @param password
     * @return
     */
    private String testCustomRealm(String username, String password) {
        return login(username, password, "classpath:shiro-realm.ini");
    }

    /**
     * 自定义多个Realm数据源
     *
     * 1.securityManager 会按照 realms 指定的顺序进行身份认证，如shiro-multi-realm.ini中，显示指定了顺序 》 securityManager.realms=$myRealm,$myRealm2
     * 2.如果删除 “securityManager.realms=$myRealm1,$myRealm2”，那么securityManager 会按照 realm 声明的顺序进行使用
     * 3.当我们显示指定 realm 后，其他没有指定 realm 将被忽略，如 “securityManager.realms=$myRealm1”，那么 myRealm2 不会被自动设置进去
     * @param username
     * @param password
     * @return
     */
    private String testCustomMultiRealm(String username, String password) {
        return login(username, password, "classpath:shiro-multi-realm.ini");
    }

    private String testJdbcRealm(String username, String password) {
        return login(username, password, "classpath:shiro-jdbc-realm.ini");
    }

    private String login(String username, String password, String configFile) {
        // 1.获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
        Factory<SecurityManager> factory = new IniSecurityManagerFactory(configFile);
        // 2.得到SecurityManager，并绑定给SecurityUtils
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager); // 这是一个全局设置，设置一次即可
        // 3.得到Subject，创建用户名/密码身份验证Token（即用户身份/凭证）
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            // 4.登录（身份验证）
            subject.login(token); // 会自动委托给 SecurityManager.login 方法进行登录
        } catch (AuthenticationException e) {
            // 5.身份验证失败，请捕获 AuthenticationException 或其子类
            log.info(e.getMessage());
        }
        Assert.isTrue(subject.isAuthenticated(), "登录失败，用户名或密码错误"); // 断言用户已登录
        log.info("是否登录成功：" + subject.isAuthenticated());
        // 6.退出登录
        subject.logout();
        return "登录成功";
    }
}
