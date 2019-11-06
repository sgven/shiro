package demo.shiro.encrypt.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class MyRealm extends AuthorizingRealm {

    /**
        为了方便，直接注入一个 passwordService 来加密密码；
        1.实际使用passwordService时，需要在 Service 层使用 passwordService 加密密码并存到数据库
        2.实际使用自定义Realm（安全数据源）验证时，注入 Service 层服务，根据用户名从数据库取得用户信息（User user = userService.getUser(username);）
            在身份验证方法doGetAuthenticationInfo中返回验证信息
            doGetAuthenticationInfo(token) {
                User user = userService.getUser(token.getPrincipal());
                if (user==null) return null; //用户不存在直接登录失败
                return new SimpleAuthenticationInfo(user.username, user.password, realmName: getName());
            }
        3.后续做密码验证，对比token和info中的密码（credentials） 》 AuthenticatingRealm.assertCredentialsMatch(token, info) 》 boolean CredentialsMatcher.doCredentialsMatch(token, info)
        4.对于使用自定义CredentialsMatcher的场景（自定义匹配器matcher实现，简单的可以继承SimpleCredentialsMatcher，有hash需求的就继承HashedCredentialsMatcher）
        例如：
            场景1：自定义密码验证
                 doCredentialsMatch(token,info) {
                     // 获取salt
                     // 给token请求密码加密，看需要选择一种加密算法
                     // 验证密码是否正确
                     return token.pwd.equals(info.pwd);
                 }
            场景2：使用第三方验证登录（多端验证登录），需要自定义密码校验规则。
                 doCredentialsMatch(token,info) {
                    /**
                        由于当前应用不存储第三方系统的用户信息，且也不知第三方系统密码加密的规则，
                        所以为了方便开发，第三方校验暂且在realm中完成。即realm中返回info，matcher中简单返回true即可，暂且不做密码校验。
                        后续有了接口，再调第三方接口校验密码
                    /
                    return true;
                 }
     */
    private PasswordService passwordService;

    public void setPasswordService(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 这里为了方便测试，Info中的username和password，实际开发时应从数据库取得，根据token的username从数据库取得UserInfo
        return new SimpleAuthenticationInfo(
                "wu",
                passwordService.encryptPassword("123"),
                getName());
    }
}