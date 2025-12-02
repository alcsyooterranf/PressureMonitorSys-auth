package org.pms.auth.trigger;

import com.pms.types.HttpResponse;
import com.pms.types.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.pms.auth.infrastructure.utils.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 公钥接口
 * 提供公钥给其他服务使用
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/11/26
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class PublicKeyController {

    /**
     * 获取公钥（Base64编码）
     * 其他服务启动时调用此接口获取公钥
     *
     * GET /auth/publicKey
     *
     * @return 公钥信息
     */
    @GetMapping("/publicKey")
    public HttpResponse<Map<String, String>> getPublicKey() {
        try {
            // 使用JwtUtil提供的方法获取Base64编码的公钥
            String publicKeyBase64 = JwtUtil.getPublicKeyStr();

            Map<String, String> data = new HashMap<>();
            data.put("publicKey", publicKeyBase64);
            data.put("algorithm", "RSA");
            data.put("keySize", "2048");
            data.put("format", "X.509");

            log.info("公钥获取成功");
            return HttpResponse.<Map<String, String>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .message(ResponseCode.SUCCESS.getMessage())
                    .data(data)
                    .build();
        } catch (Exception e) {
            log.error("获取公钥失败", e);
            return HttpResponse.<Map<String, String>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .message("获取公钥失败: " + e.getMessage())
                    .build();
        }
    }
}

