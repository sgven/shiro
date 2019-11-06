package demo.shiro.encrypt.credentials;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

import java.util.concurrent.atomic.AtomicInteger;

public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    // Ehcache是纯Java的进程内缓存框架，具有快速、精干等特点，Java分布式缓存，主要面向通用缓存
    private Ehcache passwordRetryCache;

    public RetryLimitHashedCredentialsMatcher() {
        CacheManager cacheManager = CacheManager.newInstance(CacheManager.class.getClassLoader().getResource("ehcache.xml"));
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String) token.getPrincipal();
        // retry count + 1
        Element element = passwordRetryCache.get(username);
        if (element == null) {
            element = new Element(username, new AtomicInteger(0));
            passwordRetryCache.put(element);
        }
        AtomicInteger retryCount = (AtomicInteger) element.getObjectValue();
        if (retryCount.incrementAndGet() > 5) {
            // if retry count > 5 throw
            throw new ExcessiveAttemptsException(); // 登录重试次数，超限。只允许在一段时间内允许有一定数量的认证尝试
        }
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            // clean retry count
            passwordRetryCache.remove(username);
        }
        return matches;
    }
}
