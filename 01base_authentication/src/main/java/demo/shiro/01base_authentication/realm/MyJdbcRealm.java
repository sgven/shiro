package demo.shiro.realm;

//import demo.shiro.dto.UserDTO;
//import demo.shiro.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Autowired;

public class MyJdbcRealm implements Realm {

//    @Autowired
//    private UserService userService;

    @Override
    public String getName() {
        return "myJdbcRealm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal(); // 身份(用户名、手机号、邮箱，唯一即可)
        String password = new String((char[]) token.getCredentials()); // 凭证(密码、数字证书)
        // TODO: 2019/10/30 查询数据库，验证密码
//        UserDTO user = userService.getUserByUserName(username);
        // 认证成功，返回一个AuthenticationInfo的实现
        return new SimpleAuthenticationInfo(username, password, getName());// username，password，realmName
    }
}
