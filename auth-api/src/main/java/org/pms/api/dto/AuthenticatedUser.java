package org.pms.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 认证用户信息（纯Java实现，不依赖Spring Security）
 * 用于JWT验证后封装用户信息和权限
 * <p>
 * 与 LoginUser 的区别：
 * - 不实现 UserDetails 接口
 * - 不依赖 Spring Security
 * - 权限使用 List&lt;String&gt; 而非 GrantedAuthority
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/12/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser implements Serializable {
	
	/**
	 * 用户基本信息
	 */
	private UserAggregate userAggregate;
	
	/**
	 * 权限列表（字符串形式）
	 * 例如：["user:read", "user:write", "ROLE_ADMIN"]
	 */
	private List<String> authorities;
	
	/**
	 * 账号是否被锁定
	 */
	@Builder.Default
	private boolean accountNonLocked = true;
	
	/**
	 * 账号是否过期
	 */
	@Builder.Default
	private boolean accountNonExpired = true;
	
	/**
	 * 账号是否启用
	 */
	public boolean isEnabled() {
		return this.accountNonExpired && this.accountNonLocked;
	}
	
	/**
	 * 获取用户ID
	 */
	public Long getUserId() {
		return userAggregate != null ? userAggregate.getId() : null;
	}
	
	/**
	 * 获取用户名
	 */
	public String getUsername() {
		return userAggregate != null ? userAggregate.getUsername() : null;
	}
	
	/**
	 * 获取角色名
	 */
	public String getRoleName() {
		return userAggregate != null ? userAggregate.getRoleName() : null;
	}
	
	/**
	 * 检查是否拥有指定权限
	 *
	 * @param permission 权限字符串
	 * @return 是否拥有该权限
	 */
	public boolean hasAuthority(String permission) {
		return authorities != null && authorities.contains(permission);
	}
	
	/**
	 * 检查是否拥有指定角色
	 *
	 * @param role 角色名（不需要ROLE_前缀）
	 * @return 是否拥有该角色
	 */
	public boolean hasRole(String role) {
		return authorities != null && authorities.contains("ROLE_" + role);
	}
	
}

