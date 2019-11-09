package demo.shiro.authentication.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.ThreadContext;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "auth")
@Slf4j
public class AuthenticatorTestController {

    @RequestMapping(value = "allSuccessWithSuccess")
    public String allSuccessWithSuccess(@RequestParam String username,@RequestParam String password) {
        return testAllSuccessWithSuccess(username, password);
    }

    @RequestMapping(value = "allSuccessWithFail")
    public String allSuccessWithFail(@RequestParam String username,@RequestParam String password) {
        return testAllSuccessWithFail(username, password);
    }

    @RequestMapping(value = "atLeastOneSuccess")
    public String atLeastOneSuccess(@RequestParam String username,@RequestParam String password) {
        return testAtLeastOneSuccess(username, password);
    }

    @RequestMapping(value = "firstSuccess")
    public String firstSuccess(@RequestParam String username,@RequestParam String password) {
        return testFirstSuccess(username, password);
    }

    @RequestMapping(value = "onlyOneSuccess")
    public String onlyOneSuccess(@RequestParam String username,@RequestParam String password) {
        return testOnlyOneSuccess(username, password);
    }

    @RequestMapping(value = "atLeastTwoSuccess")
    public String atLeastTwoSuccess(@RequestParam String username,@RequestParam String password) {
        return testAtLeastTwoSuccess(username, password);
    }

    private String testAllSuccessWithSuccess(String username, String password) {
        login(username, password,"classpath:shiro-authenticator-all-success.ini");
        Subject subject = SecurityUtils.getSubject();
        //得到一个身份集合，其包含了Realm验证成功的身份信息
        PrincipalCollection principalCollection = subject.getPrincipals();
        Assert.isTrue((principalCollection !=null ? principalCollection.asList().size() : 0) == 2, "验证失败");
        try {
            tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "身份验证成功";
    }

    private String testAllSuccessWithFail(String username, String password) {
        login(username, password,"classpath:shiro-authenticator-all-fail.ini");
        Subject subject = SecurityUtils.getSubject();
        //得到一个身份集合，其包含了Realm验证成功的身份信息
        PrincipalCollection principalCollection = subject.getPrincipals();
        Assert.isTrue((principalCollection !=null ? principalCollection.asList().size() : 0) == 2, "验证失败");
        try {
            tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "身份验证成功";
    }

    private String testAtLeastOneSuccess(String username, String password) {
        login(username, password, "classpath:shiro-authenticator-atLeastOne-success.ini");
        Subject subject = SecurityUtils.getSubject();
        //得到一个身份集合，其包含了Realm验证成功的身份信息
        PrincipalCollection principalCollection = subject.getPrincipals();
        Assert.isTrue((principalCollection !=null ? principalCollection.asList().size() : 0) == 2, "验证失败");
        try {
            tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "身份验证成功";
    }

    private String testFirstSuccess(String username, String password) {
        login(username, password, "classpath:shiro-authenticator-first-success.ini");
        Subject subject = SecurityUtils.getSubject();
        //得到一个身份集合，其包含了第一个Realm验证成功的身份信息
        PrincipalCollection principalCollection = subject.getPrincipals();
        Assert.isTrue((principalCollection !=null ? principalCollection.asList().size() : 0) == 1, "验证失败");
        try {
            tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "身份验证成功";
    }

    private String testOnlyOneSuccess(String username, String password) {
        login(username, password, "classpath:shiro-authenticator-onlyone-success.ini");
        Subject subject = SecurityUtils.getSubject();
        //得到一个身份集合，其包含了Realm验证成功的身份信息
        PrincipalCollection principalCollection = subject.getPrincipals();
        Assert.isTrue((principalCollection !=null ? principalCollection.asList().size() : 0) == 1, "验证失败");
        try {
            tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "身份验证成功";
    }

    private String testAtLeastTwoSuccess(String username, String password) {
        login(username, password, "classpath:shiro-authenticator-atLeastTwo-success.ini");
        Subject subject = SecurityUtils.getSubject();
        //得到一个身份集合，因为myRealm1和myRealm4返回的身份一样所以输出时只返回一个
        PrincipalCollection principalCollection = subject.getPrincipals();
        Assert.isTrue((principalCollection !=null ? principalCollection.asList().size() : 0) == 1, "验证失败");
        try {
            tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "身份验证成功";
    }

    private void login(String username, String password, String configFile) {
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

    private void tearDown() throws Exception {
        ThreadContext.unbindSubject();//退出时请解除绑定Subject到线程 否则对下次测试造成影响
    }
}
