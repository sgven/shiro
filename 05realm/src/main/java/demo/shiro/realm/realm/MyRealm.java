package demo.shiro.realm.realm;

import demo.shiro.realm.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;

public class MyRealm extends AuthorizingRealm {
    @Override
    public String getName() {
        return "myRealm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();

        /**
         *  todo: 根据用户名在数据库查询User信息
         *         if (user == null) {
         *             throw new UnknownAccountException();
         *         }
        */
        /**
         * 为了测试方便，模拟用户
         */
        if (!"zhang".equals(username)) {
            throw new UnknownAccountException();
        }
        User user = User.builder().username("zhang").password("123").build();

        return new SimpleAuthenticationInfo(
                user.getUsername(), //身份 字符串类型
                user.getPassword(),   //凭据
                getName() //Realm Name
        );
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }
}
