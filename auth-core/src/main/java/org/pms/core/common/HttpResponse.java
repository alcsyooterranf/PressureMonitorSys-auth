package org.pms.core.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author alcsyooterranf
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class HttpResponse<T> {
	
	private String code;
	private String message;
	private T data;
	
}
