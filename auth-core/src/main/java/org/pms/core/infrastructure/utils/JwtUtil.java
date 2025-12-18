package org.pms.core.infrastructure.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.pms.core.domain.model.entity.LoginUser;
import org.pms.core.initialization.AuthRunner;
import org.pms.types.AuthCode;
import org.pms.types.AuthConstants;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

/**
 * JWT工具类（Auth服务专用）
 * Auth服务持有私钥和公钥，用于签发和验证JWT
 * <p>
 * 注意：密钥由 {@link AuthRunner} 统一初始化管理
 *
 * @author alcsyooterranf
 */
@Slf4j
public class JwtUtil {
	
	private static final SignatureAlgorithm ALGORITHM = Jwts.SIG.RS256;
	private static final Long REFRESH_EXPIRATION = AuthConstants.REFRESH_EXPIRATION;
	private static final String ISS = AuthConstants.ISS;
	private static final String USER_ID = AuthConstants.USER_ID;
	private static final String USER_NAME = AuthConstants.USER_NAME;
	private static final String AUTHORITIES = AuthConstants.AUTHORITIES;
	
	// 私有构造函数，防止实例化
	private JwtUtil() {
	}
	
	/**
	 * 获取公钥（从 ApplicationInitializer 获取）
	 */
	private static PublicKey getPublicKey() {
		return AuthRunner.getPublicKey();
	}
	
	/**
	 * 获取私钥（从 ApplicationInitializer 获取）
	 */
	private static PrivateKey getPrivateKey() {
		return AuthRunner.getPrivateKey();
	}
	
	/**
	 * 获取Base64编码的公钥字符串
	 *
	 * @return Base64编码的公钥
	 */
	public static String getPublicKeyStr() {
		return Base64.getEncoder().encodeToString(getPublicKey().getEncoded());
	}
	
	/**
	 * 根据用户信息生成token,存入用户信息
	 *
	 * @param user 用户信息
	 * @return token
	 */
	public static String generateToken(LoginUser user, String uuid, Long expiration) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(USER_ID, user.getUserAggregate().getId());                           // 用户ID
		claims.put(USER_NAME, user.getUsername());                                      // 用户名
		claims.put(AUTHORITIES, user.getAuthorities());                                 // 用户权限
		
		return Jwts.builder()
				.claims(claims)                                                         // 自定义payload信息
				.issuer(ISS)                                                            // jwt签发者
				.id(uuid)                                                               // jwt唯一标识jti
				.issuedAt(new Date())                                                   // jwt签发时间
				.expiration(new Date(System.currentTimeMillis() + expiration * 1000))   // jwt过期时间
				.signWith(getPrivateKey(), ALGORITHM)                                   // 签名算法
				.compact();                                                             // 格式压缩
	}
	
	/**
	 * 刷新token
	 *
	 * @param refreshToken 刷新token
	 * @return 新token
	 */
	public static String refreshToken(String refreshToken, Long expiration) {
		Claims claims = getClaimsFromToken(refreshToken);
		
		return Jwts.builder()
				.claims(claims)
				.issuer(ISS)
				.id(UUID.randomUUID().toString())
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expiration * 1000))
				.signWith(getPrivateKey(), ALGORITHM)
				.compact();
	}
	
	/**
	 * 判断token是否可以被刷新
	 *
	 * @param token token
	 * @return 是否可以刷新
	 */
	public static boolean canRefresh(String token) {
		Claims claims = getClaimsFromToken(token);
		if (claims == null) {
			// TODO: 异常处理
		}
		assert claims != null;
		Date expireDate = claims.getExpiration();
		Date now = new Date();
		// 先判断是否过期, 如果过期, 则返回false; 如果未过期, 则判断是否在刷新期内
		return !expireDate.before(now) && expireDate.getTime() - now.getTime() < 60 * 1000 * 60 * 24;
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
	 * 从token中获取荷载, 出现异常时返回空值claims
	 *
	 * @param token token
	 * @return 荷载
	 */
	private static Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser()
					.verifyWith(getPublicKey())
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
