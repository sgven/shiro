package demo.shiro.authentication.strategy;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.pam.AbstractAuthenticationStrategy;
import org.apache.shiro.realm.Realm;

import java.util.Collection;

/**
 * 对源码的注释，没有额外的逻辑
 * 父类AbstractAuthenticationStrategy的默认实现逻辑 》 以下注释中简称——》默认实现
 *
 * 自定义 AuthenticationStrategy 实现，首先看其 API：
 *
 * //在所有Realm验证之前调用
 * AuthenticationInfo beforeAllAttempts(Collection<? extends Realm> realms, AuthenticationToken token) throws AuthenticationException;
 * //在每个Realm之前调用
 * AuthenticationInfo beforeAttempt(Realm realm, AuthenticationToken token, AuthenticationInfo aggregate) throws AuthenticationException;
 * //在每个Realm之后调用
 * AuthenticationInfo afterAttempt(Realm realm, AuthenticationToken token, AuthenticationInfo singleRealmInfo, AuthenticationInfo aggregateInfo, Throwable t)
 *             throws AuthenticationException;
 * //在所有Realm之后调用
 * AuthenticationInfo afterAllAttempts(AuthenticationToken token, AuthenticationInfo aggregate) throws AuthenticationException;
 *
 * 自定义验证策略实现，一般继承AbstractAuthenticationStrategy即可，具体参考OnlyOneAuthenticatorStrategy 和 AtLeastTwoAuthenticatorStrategy
 */
public class AbstractStrategy extends AbstractAuthenticationStrategy {

    /**
     * 默认实现：返回一个认证信息容器，供后面Realm保存认证信息
     */
    @Override
    public AuthenticationInfo beforeAllAttempts(Collection<? extends Realm> realms, AuthenticationToken token) throws AuthenticationException {
        return new SimpleAuthenticationInfo();
    }

    /**
     * 默认实现：单个Realm认证前，对前面的认证信息结果不做处理，直接返回原结果
     */
    @Override
    public AuthenticationInfo beforeAttempt(Realm realm, AuthenticationToken token, AuthenticationInfo aggregate) throws AuthenticationException {
        return aggregate;
    }

    /**
     * 默认实现：在单个Realm认证后，对认证后的信息和以前的认证信息进行处理
     * @param realm
     * @param token
     * @param singleRealmInfo   本次的验证信息
     * @param aggregateInfo     以前的认证信息
     * @param t
     * @return
     * @throws AuthenticationException
     */
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
            }
        }

        return info;
    }

    /**
     * 默认实现：当所有Realm都验证完后，直接返回结果
     * @param token
     * @param aggregate
     * @return
     * @throws AuthenticationException
     */
    @Override
    public AuthenticationInfo afterAllAttempts(AuthenticationToken token, AuthenticationInfo aggregate) throws AuthenticationException {
        return aggregate;
    }
}
