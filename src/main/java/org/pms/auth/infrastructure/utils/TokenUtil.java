package org.pms.auth.infrastructure.utils;

import com.pms.types.AppException;
import com.pms.types.Constants;
import com.pms.types.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.pms.auth.domain.model.entity.LoginUser;
import org.pms.auth.domain.model.valobj.UserTokenVO;
import org.pms.auth.infrastructure.redis.RedisUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author alcsyooterranf
 */
@Slf4j
@Service
public class TokenUtil {
	
	private static final Long ACCESS_EXPIRATION = Constants.ACCESS_EXPIRATION;
	private static final Long REFRESH_EXPIRATION = Constants.REFRESH_EXPIRATION;
	private static final String REDIS_KEY_PREFIX_ACCESS = Constants.REDIS_KEY_PREFIX_ACCESS;
	private static final String REDIS_KEY_PREFIX_REFRESH = Constants.REDIS_KEY_PREFIX_REFRESH;
	
	private final RedisUtil redisUtil;
	
	public TokenUtil(RedisUtil redisUtil) {
		this.redisUtil = redisUtil;
	}
	
	public String genAccessToken(LoginUser user, String jti) {
		return JwtUtil.generateToken(user, jti, ACCESS_EXPIRATION);
	}
	
	public String genRefreshToken(LoginUser user, String jti) {
		return JwtUtil.generateToken(user, jti, REFRESH_EXPIRATION);
	}
	
	public void saveAccessToken(String jti, String token) {
		saveToken(REDIS_KEY_PREFIX_ACCESS, jti, token, ACCESS_EXPIRATION);
	}
	
	public void saveRefreshToken(String jti, String token) {
		saveToken(REDIS_KEY_PREFIX_REFRESH, jti, token, REFRESH_EXPIRATION);
	}
	
	/**
	 * 需要捕获redis异常
	 *
	 * @param prefix     token前缀
	 * @param jti        tokenId
	 * @param token      token
	 * @param expiration 过期时间
	 */
	private void saveToken(String prefix, String jti, String token, Long expiration) {
		redisUtil.set(prefix + jti, token, expiration);
	}
	
	/**
	 * 根据 refreshToken 刷新 accessToken
	 *
	 * @param tokenVO tokenVO
	 */
	public void refreshToken(UserTokenVO tokenVO) {
		String refreshToken = tokenVO.getRefreshToken();
		String jti = JwtUtil.getJTIFromToken(refreshToken);
		// 1. refreshToken存在性检验
		isRefreshTokenExist(jti);
		// 2. 生成新的accessToken
		String accessToken = JwtUtil.refreshToken(refreshToken, ACCESS_EXPIRATION);
//        // 3. 如果refreshToken快过期了，则同时刷新refreshToken
//        if (JwtUtil.canRefresh(refreshToken)) {
//            redisUtil.expire(REDIS_KEY_PREFIX_REFRESH + jti, REFRESH_EXPIRATION);
//        }
		// 4.更新UserTokenVO
		tokenVO.setAccessToken(accessToken);
//        tokenVO.setRefreshToken(refreshToken);
	}
	
	public boolean removeToken(String token) {
		String jti = JwtUtil.getJTIFromToken(token);
		SecurityContextHolder.clearContext();
		return redisUtil.del(REDIS_KEY_PREFIX_REFRESH + jti);
	}
	
	/**
	 * 判断refreshToken是否存在
	 *
	 * @param jti jwtTokenId
	 */
	public void isRefreshTokenExist(String jti) {
		if (!redisUtil.hasKey(REDIS_KEY_PREFIX_REFRESH + jti)) {
			log.error("异常代码: {}, 异常信息: {}", ResponseCode.REFRESH_TOKEN_NOT_EXIST.getCode(),
					ResponseCode.REFRESH_TOKEN_NOT_EXIST.getMessage());
			throw new AppException(ResponseCode.REFRESH_TOKEN_NOT_EXIST);
		}
	}
	
}
