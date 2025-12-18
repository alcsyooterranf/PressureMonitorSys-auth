package org.pms.core.domain.service.impl;

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
		UserTokenVO userTokenVO = UserTokenVO.builder()
				.refreshToken(refreshToken)
				.publicKey64(JwtUtil.getPublicKeyStr())
				.build();
		authRepository.updateUserToken(userTokenVO);
		return userTokenVO;
	}
	
}
