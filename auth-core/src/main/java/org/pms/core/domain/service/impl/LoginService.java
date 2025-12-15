package org.pms.core.domain.service.impl;

import com.pms.types.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.pms.core.domain.model.valobj.UserTokenVO;
import org.pms.core.domain.repository.IAuthRepository;
import org.pms.core.domain.service.ILoginService;
import org.pms.core.infrastructure.utils.JwtUtil;
import org.springframework.stereotype.Service;

/**
 * @author alcsyooterranf
 */
@Slf4j
@Service
public class LoginService implements ILoginService {
	
	private final IAuthRepository authRepository;
	
	public LoginService(IAuthRepository authRepository) {
		this.authRepository = authRepository;
	}
	
	/**
	 * 根据refreshToken刷新accessToken
	 *
	 * @param refreshToken 刷新token
	 * @return accessToken
	 */
	@Override
	public UserTokenVO doRefresh(String refreshToken) {
		AssertUtils.notNull(refreshToken, "refreshToken不能为空");
		UserTokenVO userTokenVO = UserTokenVO.builder()
				.refreshToken(refreshToken)
				.publicKey64(JwtUtil.getPublicKeyStr())
				.build();
		authRepository.updateUserToken(userTokenVO);
		AssertUtils.notNull(userTokenVO.getAccessToken(), "刷新失败");
		return userTokenVO;
	}
	
}
