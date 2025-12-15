package org.pms.api;

import org.pms.api.common.RpcResponse;

/**
 * Auth服务RPC接口
 * 用于Gateway和WS服务调用Auth服务的内部接口
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/11/26
 */
public interface IAuthRpcService {
	
	/**
	 * 获取RSA公钥
	 * Consumer端启动时通过InitializingBean主动调用此接口获取公钥
	 *
	 * @return Base64编码的RSA公钥字符串
	 */
	RpcResponse<String> getPublicKey();
	
	/**
	 * 检查公钥是否一致
	 * Consumer端可调用此接口验证本地缓存的公钥是否与Auth服务一致
	 *
	 * @param publicKey Consumer端持有的Base64编码公钥字符串
	 * @return true-公钥一致，false-公钥不一致
	 */
	RpcResponse<Boolean> checkPublicKey(String publicKey);
	
}

