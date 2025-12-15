package org.pms.trigger.rpc;

import lombok.extern.slf4j.Slf4j;
import org.pms.api.IAuthRpcService;
import org.pms.api.common.RpcResponse;
import org.pms.core.infrastructure.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

/**
 * Auth服务RPC接口实现类 - Provider端
 * 提供内部RPC接口给Consumer端（Gateway、WS服务）调用
 *
 * @author alcsyooterranf
 * @program PressureMonitorSys-auth
 * @description Auth服务RPC接口实现
 * @create 2025/12/13
 */
@Slf4j
@RestController
@RequestMapping("/rpc/auth")
public class AuthRpcService implements IAuthRpcService {
	
	@Override
	@GetMapping("/publicKey")
	public RpcResponse<String> getPublicKey() {
		try {
			String publicKeyBase64 = JwtUtil.getPublicKeyStr();
			log.info("RPC接口：公钥获取成功");
			return RpcResponse.<String>builder()
					.code("200")
					.message("success")
					.data(publicKeyBase64)
					.build();
		} catch (Exception e) {
			log.error("RPC接口：获取公钥失败", e);
			throw new RuntimeException("获取公钥失败: " + e.getMessage(), e);
		}
	}
	
	@Override
	@PostMapping("/checkPublicKey")
	public RpcResponse<Boolean> checkPublicKey(@RequestBody String publicKey) {
		try {
			String serverPublicKey = JwtUtil.getPublicKeyStr();
			boolean isMatch = serverPublicKey.equals(publicKey);
			if (isMatch) {
				log.info("RPC接口：公钥校验成功，公钥一致");
			} else {
				log.warn("RPC接口：公钥校验失败，公钥不一致");
			}
			return RpcResponse.<Boolean>builder()
					.code("200")
					.data(isMatch)
					.build();
		} catch (Exception e) {
			log.error("RPC接口：公钥校验异常", e);
			throw new RuntimeException("公钥校验失败: " + e.getMessage(), e);
		}
	}
	
}
