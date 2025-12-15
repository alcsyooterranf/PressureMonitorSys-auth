package org.pms.trigger.controller;

import com.pms.types.Constants;
import com.pms.types.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pms.core.common.HttpResponse;
import org.pms.core.domain.model.entity.LoginUser;
import org.pms.core.domain.model.valobj.UserTokenVO;
import org.pms.core.domain.repository.IAuthRepository;
import org.pms.core.domain.service.ILoginService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务 - 登录控制器
 * 提供登录、刷新token接口
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/11/25
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginController {
	
	private static final String TOKEN_HEADER = Constants.TOKEN_HEADER;
	private static final String TOKEN_PREFIX = Constants.TOKEN_PREFIX;
	
	private final AuthenticationManager authenticationManager;
	private final IAuthRepository authRepository;
	private final ILoginService loginService;
	
	public LoginController(AuthenticationManager authenticationManager,
						   IAuthRepository authRepository,
						   ILoginService loginService) {
		this.authenticationManager = authenticationManager;
		this.authRepository = authRepository;
		this.loginService = loginService;
	}
	
	/**
	 * 用户登录接口
	 * POST /auth/login
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @return 登录结果（包含accessToken、refreshToken、publicKey64、authorities）
	 */
	@PostMapping("/login")
	public HttpResponse<Map<String, Object>> login(@RequestParam String username,
												   @RequestParam String password) {
		log.info("用户登录请求, username: {}", username);
		
		try {
			// 1. 使用Spring Security进行认证
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
			Authentication authentication = authenticationManager.authenticate(authToken);
			
			// 2. 获取认证后的用户信息
			LoginUser loginUser = (LoginUser) authentication.getPrincipal();
			if (loginUser == null) {
				log.error("认证成功但用户信息为空, username: {}", username);
				return HttpResponse.<Map<String, Object>>builder()
						.code(ResponseCode.AUTHENTICATED_USER_NOT_EXIST.getCode())
						.message(ResponseCode.AUTHENTICATED_USER_NOT_EXIST.getMessage())
						.build();
			}
			
			// 3. 生成并保存token
			authRepository.saveUserToken(loginUser);
			
			if (loginUser.getTokenVO() == null) {
				log.error("Token生成失败, username: {}", username);
				return HttpResponse.<Map<String, Object>>builder()
						.code(ResponseCode.CREATED_TOKEN_NOT_EXIST.getCode())
						.message(ResponseCode.CREATED_TOKEN_NOT_EXIST.getMessage())
						.build();
			}
			
			// 4. 构造返回结果
			Map<String, Object> result = new HashMap<>();
			result.put("authorities", loginUser.getAuthorities());
			result.put("accessToken", loginUser.getTokenVO().getAccessToken());
			result.put("refreshToken", loginUser.getTokenVO().getRefreshToken());
			result.put("publicKey64", loginUser.getTokenVO().getPublicKey64());
			
			log.info("用户登录成功, username: {}", username);
			return HttpResponse.<Map<String, Object>>builder()
					.code(ResponseCode.LOGIN_SUCCESS.getCode())
					.message(ResponseCode.LOGIN_SUCCESS.getMessage())
					.data(result)
					.build();
			
		} catch (Exception e) {
			log.error("用户登录失败, username: {}, error: {}", username, e.getMessage());
			return HttpResponse.<Map<String, Object>>builder()
					.code(ResponseCode.LOGIN_FAIL.getCode())
					.message(ResponseCode.LOGIN_FAIL.getMessage())
					.build();
		}
	}
	
	/**
	 * 刷新token接口
	 * POST /auth/refresh
	 *
	 * @param authHeader Authorization header (Bearer <refreshToken>)
	 * @return 新的accessToken
	 */
	@PostMapping("/refresh")
	public HttpResponse<UserTokenVO> refresh(@RequestHeader(TOKEN_HEADER) String authHeader) {
		log.info("刷新token请求");
		
		try {
			// 1. 提取refreshToken
			String refreshToken = StringUtils.substring(authHeader, TOKEN_PREFIX.length() + 1);
			
			if (StringUtils.isBlank(refreshToken)) {
				log.error("refreshToken为空");
				return HttpResponse.<UserTokenVO>builder()
						.code(ResponseCode.REFRESH_TOKEN_NOT_EXIST.getCode())
						.message(ResponseCode.REFRESH_TOKEN_NOT_EXIST.getMessage())
						.build();
			}
			
			// 2. 刷新token
			UserTokenVO userTokenVO = loginService.doRefresh(refreshToken);
			
			log.info("刷新token成功");
			return HttpResponse.<UserTokenVO>builder()
					.code(ResponseCode.SUCCESS.getCode())
					.message(ResponseCode.SUCCESS.getMessage())
					.data(userTokenVO)
					.build();
			
		} catch (Exception e) {
			log.error("刷新token失败, error: {}", e.getMessage());
			return HttpResponse.<UserTokenVO>builder()
					.code(ResponseCode.REFRESH_TOKEN_NOT_EXIST.getCode())
					.message(ResponseCode.REFRESH_TOKEN_NOT_EXIST.getMessage())
					.build();
		}
	}
	
	/**
	 * 测试接口
	 * GET /auth/success
	 */
	@GetMapping("/success")
	public HttpResponse<String> success() {
		log.info("测试接口调用成功");
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.data("Auth service is running")
				.build();
	}
	
}

