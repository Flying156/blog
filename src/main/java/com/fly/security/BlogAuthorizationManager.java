package com.fly.security;

import com.fly.dto.role.ResourceRoleDTO;
import com.fly.mapper.RoleMapper;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.fly.constant.GenericConst.TRUE_OF_INT;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Spring Security 授权管理器的实现
 * <p>
 *     详细参考 <a href="https://springdoc.cn/spring-security/servlet/authorization/architecture.html"></a>
 * </p>
 * @author Milk
 */
@Component
public class BlogAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext>{

    /**
     * 是否请求匿名资源
     */
    private static final ThreadLocal<Boolean> ANONYMOUS = new ThreadLocal<>();

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    // 读写公平锁
    private static final ReadWriteLock  READ_WRITE_LOCK = new ReentrantReadWriteLock(true);

    private static final Lock READ_LOCK = READ_WRITE_LOCK.readLock();

    private static final Lock WRITE_LOCK = READ_WRITE_LOCK.writeLock();

    /**
     * invalid 为 true 时，表示需要重新导入角色
     */
    private static volatile boolean invalid;


    /**
     * 接口资源和角色的信息表
     */
    private static List<ResourceRoleDTO> resourceRoleList;

    @Resource
    private RoleMapper roleMapper;


    @PostConstruct
    public void loadResourceRole(){
        resourceRoleList = roleMapper.listRoleResource();
    }

    /**
     * 清除角色表，强制下线
     * 加锁是为了防止并发下的误操作
     * 使用公平锁等待读锁的线程释放完毕后清除
     * 使用写锁防止其他进程在读或写该资源
     */
    private static void clearResourceRoleList(){
        WRITE_LOCK.lock();
        try{
            resourceRoleList = null;
            invalid = true;
        }finally {
            WRITE_LOCK.unlock();
        }
    }

    public void updateAuthorizationCredentials() {
        clearResourceRoleList();
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestContext) {
        boolean authorized;
        ANONYMOUS.set(FALSE);
        try {
            // 获取用户权限列表
            List<String> grantedList = authentication.get().getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            Set<String> availableSet = getAvailableAuthorities(requestContext);
            // 判断是否授权
            authorized = ANONYMOUS.get() || grantedList.stream().anyMatch(availableSet::contains);
        } finally {
            ANONYMOUS.remove();
        }

        return new AuthorizationDecision(authorized);
    }

    private Set<String> getAvailableAuthorities(RequestAuthorizationContext requestContext) {
        // 转为读锁，所有线程都可以读此资源
        READ_LOCK.lock();
        // 如果 authentication 中的角色已经被清除
        if(invalid){
            //
            READ_LOCK.unlock();
            WRITE_LOCK.lock();

            try{
                if(invalid){
                    loadResourceRole();
                    invalid = false;
                }
                READ_LOCK.lock();
            } finally {
                WRITE_LOCK.unlock();
            }

        }
        Set<String> availableAuthorities;
        try{
            availableAuthorities = processResourceRoleList(requestContext);
        }finally {
            READ_LOCK.unlock();
        }
        return availableAuthorities;
    }

    private Set<String> processResourceRoleList(RequestAuthorizationContext requestContext){
        // 获取请求的用户信息
        HttpServletRequest request = requestContext.getRequest();
        String url = request.getRequestURI();
        String method = request.getMethod();
        // 确认用户请求的可用权限
        for(ResourceRoleDTO resource : resourceRoleList){
            if(antPathMatcher.match(resource.getUrl(), url)
                    && antPathMatcher.match(resource.getRequestMethod(), method)){
                List<String> roleList = resource.getRoleList();
                // 匿名可访问资源
                if(TRUE_OF_INT.equals(resource.getIsAnonymous())){
                    ANONYMOUS.set(TRUE);
                }
                return new HashSet<>(roleList);
            }
        }
        return new HashSet<>();
    }
}
