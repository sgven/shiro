package demo.shiro.ini.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("config")
public class ConfigurationCreateTestController {

    @RequestMapping("iniTest")
    public void test() {
        login("classpath:shiro-config.ini");
    }

    @RequestMapping("mainIniTest")
    public void iniMainTest() {
        login("classpath:shiro-config-main.ini");
    }

    /**
     * 1.在ini配置中，默认情况先创建一个名字为 securityManager，类型为 org.apache.shiro.mgt.DefaultSecurityManager 的默认的 SecurityManager，
     * 如果想自定义，只需要在 ini 配置文件中指定 “securityManager=SecurityManager 实现类” 即可，名字必须为 securityManager，它是起始的根；
     * 2.IniSecurityManagerFactory 是创建 securityManager 的工厂，其需要一个 ini 配置文件路径，
     * 其支持 “classpath:”（类路径）、“file:”（文件系统）、“url:”（网络）三种路径格式，默认是文件系统；
     * 3.接着获取 SecuriyManager 实例，后续步骤通过SecuriyManager来使用shiro的功能
     *
     * 可以看出，Shiro INI 配置方式本身提供了一个简单的 IoC/DI 机制方便在配置文件配置，但是是从 securityManager 这个根对象开始导航
     *
     * INI配置，示例shiro.ini:
     * [main]
     * # [main] 部分提供了对根对象securityManager及其依赖的配置
     * securityManager=org.apache.shiro.mgt.DefaultSecurityManager
     * …………
     * securityManager.realms=$jdbcRealm
     * [users]
     * # [users] 部分提供了对用户/密码及其角色的配置，用户名=密码，角色1，角色2
     * username=password,role1,role2
     * [roles]
     * # [roles] 部分提供了角色及权限之间关系的配置，角色=权限1，权限2
     * role1=permission1,permission2
     * [urls]
     * # [urls]部分用于web，提供了对web url拦截相关的配置，url=拦截器[参数]，拦截器
     * /index.html = anon
     * /admin/** = authc, roles[admin], perms["permission1"]
     */
    private void login(String configFile) {
        Factory<SecurityManager> factory =
                new IniSecurityManagerFactory(configFile);

        SecurityManager securityManager = factory.getInstance();

        //将SecurityManager设置到SecurityUtils 方便全局使用
        SecurityUtils.setSecurityManager(securityManager);

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("xiejun", "123");
        subject.login(token);

        Assert.isTrue(subject.isAuthenticated(), "登录失败：账号或密码错误");
    }

}
