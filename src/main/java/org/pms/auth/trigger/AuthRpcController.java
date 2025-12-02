package org.pms.auth.trigger;

import lombok.extern.slf4j.Slf4j;
import org.pms.auth.infrastructure.utils.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auth服务RPC接口实现
 * 提供内部RPC接口给Gateway和WS服务调用
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/11/26
 */
@Slf4j
@RestController
@RequestMapping("/rpc/auth")
public class AuthRpcController {

    /**
     * 获取RSA公钥（Base64编码）
     * 供Gateway和WS服务通过Feign调用
     *
     * @return Base64编码的RSA公钥字符串
     */
    @GetMapping("/publicKey")
    public String getPublicKey() {
        try {
            String publicKeyBase64 = JwtUtil.getPublicKeyStr();
            log.info("RPC接口：公钥获取成功");
            return publicKeyBase64;
        } catch (Exception e) {
            log.error("RPC接口：获取公钥失败", e);
            throw new RuntimeException("获取公钥失败: " + e.getMessage(), e);
        }
    }
}

