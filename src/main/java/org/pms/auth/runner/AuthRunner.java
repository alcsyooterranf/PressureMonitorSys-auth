package org.pms.auth.runner;

import lombok.extern.slf4j.Slf4j;
import org.pms.auth.infrastructure.utils.RSAUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 应用启动初始化器
 * 实现 InitializingBean 接口，用于统一管理应用启动时的初始化逻辑
 * 初始化顺序可通过 @Order 注解控制，数值越小优先级越高
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2024/11/28
 */
@Slf4j
@Component
public class AuthRunner implements InitializingBean {
	
	// RSA密钥对（供 JwtUtil 使用）
	private static PublicKey publicKey;
	private static PrivateKey privateKey;
	private static volatile boolean initialized = false;
	
	/**
	 * 获取RSA公钥
	 *
	 * @return RSA公钥
	 * @throws IllegalStateException 如果密钥尚未初始化
	 */
	public static PublicKey getPublicKey() {
		checkInitialized();
		return publicKey;
	}
	
	/**
	 * 获取RSA私钥
	 *
	 * @return RSA私钥
	 * @throws IllegalStateException 如果密钥尚未初始化
	 */
	public static PrivateKey getPrivateKey() {
		checkInitialized();
		return privateKey;
	}
	
	private static void checkInitialized() {
		if (!initialized) {
			throw new IllegalStateException("ApplicationInitializer 尚未完成初始化，请确保在 Spring 容器启动完成后调用");
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("========== 应用初始化开始 ==========");
		
		// 1. 初始化RSA密钥对
		initRSAKeys();
		
		initialized = true;
		log.info("========== 应用初始化完成 ==========");
	}
	
	/**
	 * 初始化RSA密钥对
	 */
	private void initRSAKeys() {
		log.info("开始初始化RSA密钥...");
		try {
			KeyPair keyPair = RSAUtil.getKeyPair();
			publicKey = keyPair.getPublic();
			privateKey = keyPair.getPrivate();
			log.info("RSA密钥初始化成功");
		} catch (Exception e) {
			log.error("RSA密钥初始化失败: {}", e.getMessage(), e);
			throw new RuntimeException("RSA密钥初始化失败，应用无法启动", e);
		}
	}
	
}

