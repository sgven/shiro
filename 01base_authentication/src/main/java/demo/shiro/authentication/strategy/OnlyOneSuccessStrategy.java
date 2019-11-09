package demo.shiro.authentication.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.AbstractAuthenticationStrategy;
import org.apache.shiro.realm.Realm;

/**
 * 唯一realm来源验证成功策略
 */
@Slf4j
public class OnlyOneSuccessStrategy extends AbstractAuthenticationStrategy {

    @Override
    public AuthenticationInfo afterAttempt(Realm realm, AuthenticationToken token, AuthenticationInfo singleRealmInfo, AuthenticationInfo aggregateInfo, Throwable t) throws AuthenticationException {
        AuthenticationInfo info;
        if (singleRealmInfo == null) {
            info = aggregateInfo;
        } else {
            if (aggregateInfo == null) { // 如果前面的验证信息为空，则返回本次的验证信息
                info = singleRealmInfo;
            } else { // 如果前面和本次的验证信息都不为空，则合并两次的验证结果
                info = merge(singleRealmInfo, aggregateInfo);
                // 如果身份来源>1，则验证失败
                if(info.getPrincipals().getRealmNames().size() > 1) {
                    info.getPrincipals().getRealmNames().forEach(realmName -> log.info(realmName));
                    throw new AuthenticationException("Authentication token of type [" + token.getClass() + "] " +
                            "could not be authenticated by any configured realms.  Please ensure that only one realm can " +
                            "authenticate these tokens.");
                }
            }
        }
        return info;
    }

}
