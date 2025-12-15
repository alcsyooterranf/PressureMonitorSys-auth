package org.pms.api.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一RPC响应对象
 * 用于Gateway和后端服务之间的RPC通信
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/11/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {
	
	/**
	 * 响应码
	 */
	private String code;
	
	/**
	 * 响应消息
	 */
	private String message;
	
	/**
	 * 响应数据
	 */
	private T data;
	
}

