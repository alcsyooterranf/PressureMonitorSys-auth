package org.pms.api.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.dto.UserAggregate;
import org.pms.types.AuthCode;
import org.pms.types.AuthConstants;

import java.security.PublicKey;
import java.util.List;

/**
 * JWT工具类（验签专用）
 * 用于Gateway和WS服务验证JWT签名
 * 只持有公钥，不负责签发token
 * 这是一个纯Java工具类，不依赖Spring框架
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2023/7/11 20:38
 */
@Slf4j
public class JwtUtil {
	
	private static final Long REFRESH_EXPIRATION = AuthConstants.REFRESH_EXPIRATION;
	private static final String ISS = AuthConstants.ISS;
	private static final String USER_ID = AuthConstants.USER_ID;
	private static final String USER_NAME = AuthConstants.USER_NAME;
	private static final String AUTHORITIES = AuthConstants.AUTHORITIES;
	private static PublicKey PUBLIC_KEY;
	
	/**
	 * 初始化公钥
	 * 由调用方在获取公钥文件后调用
	 */
	public static void initKey() {
		try {
			PUBLIC_KEY = RSAUtil.getPublicKey();
			log.info("JwtUtil: 初始化RSA公钥成功");
		} catch (Exception e) {
			log.error("初始化RSA公钥失败: {}", e.getMessage());
			throw new IllegalStateException("无法初始化RSA公钥", e);
		}
	}
	
	/**
	 * 从token中获取UserAggregate对象（纯Java对象，不依赖Spring Security）
	 *
	 * @param token JWT token
	 * @return UserAggregate对象
	 */
	public static UserAggregate getUserAggregateFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		
		return UserAggregate.builder()
				.id(Long.parseLong(claims.get(USER_ID).toString()))
				.username((String) claims.get(USER_NAME))
				.build();
	}
	
	/**
	 * 从token中获取权限列表（字符串形式）
	 *
	 * @param token JWT token
	 * @return 权限字符串列表
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getAuthoritiesFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		return claims.get(AUTHORITIES, List.class);
	}
	
	/**
	 * 验证token是否有效并判断token是否为refreshToken
	 *
	 * @param token token
	 * @return 若为refreshToken, 则返回其jti; 否则返回null
	 */
	public static String validateToken(String token) {
		// 1. 验证签名, 先检查系统级别的三个异常
		Claims claims = getClaimsFromToken(token);
		
		// 2. 验证签发人, 检查用户级别异常
		if (!ISS.equals(claims.getIssuer())) {
			throw new RuntimeException(AuthCode.TOKEN_ISSUER_ERROR.getMessage());
		}
		
		// 3. token未过期, 根据过期时间计算token是否为refreshToken, 如果是则需要验证refreshToken存在性
		long expireTime = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
		if (expireTime == (REFRESH_EXPIRATION * 1000)) {
			return getJTIFromToken(token);
		}
		return null;
	}
	
	/**
	 * 从token中获取JTI
	 *
	 * @param token token
	 * @return JTI
	 */
	public static String getJTIFromToken(String token) {
		return getClaimsFromToken(token).getId();
	}
	
	/**
	 * 从token中获取荷载, 出现异常时抛出AppException
	 *
	 * @param token token
	 * @return 荷载
	 */
	public static Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser()
					.verifyWith(PUBLIC_KEY)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (JwtException e) {
			if (e instanceof ExpiredJwtException) {
				log.error("异常代码: {}, 异常信息: {}", AuthCode.TOKEN_EXPIRED.getCode(),
						AuthCode.TOKEN_EXPIRED.getMessage());
				throw new RuntimeException(AuthCode.TOKEN_EXPIRED.getMessage(), e);
			} else if (e instanceof SignatureException) {
				log.error("异常代码: {}, 异常信息: {}", AuthCode.TOKEN_TAMPERED.getCode(),
						AuthCode.TOKEN_TAMPERED.getMessage());
				throw new RuntimeException(AuthCode.TOKEN_TAMPERED.getMessage(), e);
			} else {
				log.error("异常代码: {}, 异常信息: {}", AuthCode.TOKEN_PARSE_ERROR.getCode(),
						AuthCode.TOKEN_PARSE_ERROR.getMessage());
				throw new RuntimeException(AuthCode.TOKEN_PARSE_ERROR.getMessage(), e);
			}
		}
		return claims;
	}
	
}

