package org.pms.core.domain.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;
import org.pms.core.domain.model.aggregate.UserAggregate;
import org.pms.core.domain.model.valobj.UserTokenVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author alcsyooterranf
 */
@Data
@Builder
public class LoginUser implements UserDetails {
	
	private UserAggregate userAggregate;
	@JSONField(serialize = false)
	private List<SimpleGrantedAuthority> authorities;
	// 账号是否被锁定
	@Builder.Default
	private boolean accountNonLocked = true;
	// 账号是否被删除
	@Builder.Default
	private boolean accountNonExpired = true;
	private UserTokenVO tokenVO;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		// 如果授权集合不为空, 则直接返回
		if (this.authorities != null) {
			return this.authorities;
		}
		
		// 如果权限集合不为空, 则将权限集合转换为授权集合
		if (!ObjectUtils.isEmpty(this.userAggregate.getPermissions())) {
			this.authorities = this.userAggregate.getPermissions()
					.stream()
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
		}
		// 如果该用户没有权限, 则初始化authorities, 并将角色添加进权限中
		if (ObjectUtils.isEmpty(this.authorities)) {
			this.authorities = new ArrayList<>();
		}
		// 角色也是一种特殊的权限
		if (!ObjectUtils.isEmpty(this.userAggregate.getRoleName()) && !"".equals(this.userAggregate.getRoleName())) {
			this.authorities.add(new SimpleGrantedAuthority("ROLE_" + this.userAggregate.getRoleName()));
		}
		return this.authorities;
	}
	
	@Override
	public String getPassword() {
		return this.userAggregate.getPassword();
	}
	
	@Override
	public String getUsername() {
		return this.userAggregate.getUsername();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonExpired;
	}
	
	@Override
	public boolean isEnabled() {
		return this.accountNonExpired && this.accountNonLocked;
	}
	
}
