package org.pms.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-auth
 * @description 返回码
 * @create 2025/12/14
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ResponseCode {
	
	SUCCESS("200", "成功"),
	
	FAIL("500", "失败"),
	
	;
	
	private String code;
	private String message;
}
