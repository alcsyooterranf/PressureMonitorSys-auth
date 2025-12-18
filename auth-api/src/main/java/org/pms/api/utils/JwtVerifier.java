package org.pms.api.utils;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.dto.AuthenticatedUser;
import org.pms.api.dto.UserAggregate;
import org.pms.types.AuthConstants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * JWT验证器（纯Java实现，不依赖Spring）
 * 封装JwtUtil，提供更高级的JWT验证和用户信息提取功能
 * 
 * 与 JwtService 的区别：
 * - 不使用 @Service 注解
 * - 返回 AuthenticatedUser 而非 LoginUser
 * - 不依赖 Spring Security
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/12/14
 */
@Slf4j
public class JwtVerifier {

    private static final String USER_ID = AuthConstants.USER_ID;
    private static final String USER_NAME = AuthConstants.USER_NAME;
    private static final String ROLE_NAME = AuthConstants.ROLE_NAME;
    private static final String AUTHORITIES = AuthConstants.AUTHORITIES;
    private static final String PERMISSIONS = AuthConstants.PERMISSIONS;

    /**
     * 初始化公钥
     * 由调用方在获取公钥文件后调用
     */
    public static void initKey() {
        JwtUtil.initKey();
        log.info("JwtVerifier: 公钥初始化完成");
    }

    /**
     * 从token中获取AuthenticatedUser对象
     *
     * @param token JWT token
     * @return AuthenticatedUser对象
     */
    @SuppressWarnings("unchecked")
    public static AuthenticatedUser getAuthenticatedUser(String token) {
        Claims claims = JwtUtil.getClaimsFromToken(token);

        // 构建UserAggregate
        UserAggregate userAggregate = UserAggregate.builder()
                .id(Long.parseLong(claims.get(USER_ID).toString()))
                .username((String) claims.get(USER_NAME))
                .roleName((String) claims.get(ROLE_NAME))
                .build();

        // 提取权限列表
        List<String> authorities = extractAuthorities(claims);

        return AuthenticatedUser.builder()
                .userAggregate(userAggregate)
                .authorities(authorities)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .build();
    }

    /**
     * 从Claims中提取权限列表
     * 支持两种格式：
     * 1. authorities: [{"authority": "user:read"}, {"authority": "user:write"}]
     * 2. permissions: ["user:read", "user:write"]
     *
     * @param claims JWT Claims
     * @return 权限字符串列表
     */
    @SuppressWarnings("unchecked")
    private static List<String> extractAuthorities(Claims claims) {
        List<String> authorities = new ArrayList<>();

        // 尝试从 authorities 字段提取（Spring Security格式）
        Object authoritiesObj = claims.get(AUTHORITIES);
        if (authoritiesObj instanceof List) {
            List<?> list = (List<?>) authoritiesObj;
            for (Object item : list) {
                if (item instanceof LinkedHashMap) {
                    LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) item;
                    String authority = map.get("authority");
                    if (authority != null) {
                        authorities.add(authority);
                    }
                } else if (item instanceof String) {
                    authorities.add((String) item);
                }
            }
        }

        // 尝试从 permissions 字段提取（简单字符串列表格式）
        Object permissionsObj = claims.get(PERMISSIONS);
        if (permissionsObj instanceof List) {
            List<String> permissions = (List<String>) permissionsObj;
            authorities.addAll(permissions);
        }

        // 添加角色权限（如果存在）
        String roleName = (String) claims.get(ROLE_NAME);
        if (roleName != null && !roleName.isEmpty()) {
            authorities.add("ROLE_" + roleName);
        }

        return authorities;
    }

    /**
     * 验证token是否有效并判断token是否为refreshToken
     *
     * @param token token
     * @return 若为refreshToken, 则返回其jti; 否则返回null
     */
    public static String validateToken(String token) {
        return JwtUtil.validateToken(token);
    }

    /**
     * 从token中获取JTI
     *
     * @param token token
     * @return JTI
     */
    public static String getJTI(String token) {
        return JwtUtil.getJTIFromToken(token);
    }

    /**
     * 从token中获取UserAggregate对象
     *
     * @param token JWT token
     * @return UserAggregate对象
     */
    public static UserAggregate getUserAggregate(String token) {
        return JwtUtil.getUserAggregateFromToken(token);
    }

    /**
     * 从token中获取Claims
     *
     * @param token JWT token
     * @return Claims对象
     */
    public static Claims getClaims(String token) {
        return JwtUtil.getClaimsFromToken(token);
    }

}

