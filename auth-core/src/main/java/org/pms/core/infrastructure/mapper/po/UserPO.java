package org.pms.core.infrastructure.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author alcsyooterranf
 */
// @Data和@Builder一起用：我们发现没有了默认的构造方法

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -9193963415142790399L;
	
	private Long id;
	private String username;
	private String password;
	private String phone;
	private Boolean locked;
	private Date createTime;
	private String createBy;
	private Date updateTime;
	private String updateBy;
	private Date deleteTime;
	private String deleteBy;
	private Boolean removed;
	
}