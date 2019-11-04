package demo.shiro.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;

public class MyRealm4 implements Realm {
    @Override
    public String getName() {
        return "myRealm4";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        // 仅支持UsernamePasswordToken类型的token
        return token instanceof UsernamePasswordToken;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal(); // 身份(用户名、手机号、邮箱，唯一即可)
        String password = new String((char[]) token.getCredentials()); // 凭证(密码、数字证书)
        if (!"xiej".equals(username)) {
            throw new UnknownAccountException(); // 用户名错误
        }
        if (!"123".equals(password)) {
            throw new IncorrectCredentialsException(); // 密码错误
        }
        // 认证成功，返回一个AuthenticationInfo的实现
        return new SimpleAuthenticationInfo(username, password, getName());// username，password，realmName
    }
}
