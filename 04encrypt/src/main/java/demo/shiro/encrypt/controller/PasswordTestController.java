package demo.shiro.encrypt.controller;

import com.sun.javafx.css.converters.EnumConverter;
import demo.shiro.encrypt.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 此方式的缺点是：salt 保存在散列值中；没有实现如密码重试次数限制。
@RestController
@RequestMapping("pwd")
@Slf4j
public class PasswordTestController extends BaseTest {

    @RequestMapping("passwordServiceWithMyRealm")
    public void testPasswordServiceWithMyRealm() {
        login("wu", "123","classpath:shiro-passwordservice.ini");
        Assert.isTrue(subject().isAuthenticated(), "登录失败： ...");
    }

    @RequestMapping("passwordServiceWithJdbcRealm")
    public void testPasswordServiceWithJdbcRealm() {
        login("wu", "123","classpath:shiro-jdbc-passwordservice.ini");
        Assert.isTrue(subject().isAuthenticated(), "登录失败： ...");
    }

    @RequestMapping("generatePassword")
    public String testGeneratePassword() {
        String algorithmName = "md5";
        String username = "liu";
        String password = "123";
        String salt1 = username;
        String salt2 = new SecureRandomNumberGenerator().nextBytes().toHex();
        int hashIterations = 2;

        SimpleHash hash = new SimpleHash(algorithmName, password, salt1 + salt2, hashIterations);
        String encodedPassword = hash.toHex();
        log.info(salt2);
        log.info(encodedPassword);
        return encodedPassword;
    }

    @RequestMapping("hashMatcherWithMyRealm2")
    public void testHashedCredentialsMatcherWithMyRealm2() {
        //使用testGeneratePassword生成的散列密码
        login("liu", "123", "classpath:shiro-hashedCredentialsMatcher.ini");
    }

    @RequestMapping("hashMatcherWithJdbcRealm")
    public void testHashedCredentialsMatcherWithJdbcRealm() {
        //注意 Shiro 默认使用了 apache commons BeanUtils，默认是不进行 Enum 类型转型的，此时需要自己注册一个 Enum 转换器
        BeanUtilsBean.getInstance().getConvertUtils().register(new EnumConverter(), JdbcRealm.SaltStyle.class);
        //使用testGeneratePassword生成的散列密码
        login("liu", "123", "classpath:shiro-jdbc-hashedCredentialsMatcher.ini");
    }

    private class EnumConverter extends AbstractConverter {
        @Override
        protected String convertToString(final Object value) throws Throwable {
            return ((Enum) value).name();
        }
        @Override
        protected Object convertToType(final Class type, final Object value) throws Throwable {
            return Enum.valueOf(type, value.toString());
        }

        @Override
        protected Class getDefaultType() {
            return null;
        }

    }

    // expected = ExcessiveAttemptsException
    @RequestMapping("retryLimitMatcherWithMyRealm")
    public void testRetryLimitHashedCredentialsMatcherWithMyRealm() {
        for(int i = 1; i <= 5; i++) {
            try {
                login("liu", "234", "classpath:shiro-retryLimitHashedCredentialsMatcher.ini");
            } catch (Exception e) {/*ignore*/}
        }
        login("liu", "234", "classpath:shiro-retryLimitHashedCredentialsMatcher.ini");
    }

}
