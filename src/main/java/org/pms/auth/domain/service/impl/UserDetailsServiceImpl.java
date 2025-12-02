package org.pms.auth.domain.service.impl;

import com.pms.types.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.pms.auth.domain.model.aggregate.UserAggregate;
import org.pms.auth.domain.model.entity.LoginUser;
import org.pms.auth.domain.model.entity.UserEntity;
import org.pms.auth.domain.repository.IUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetails实现，负责用户认证
 *
 * @author alcsyooterranf
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final IUserRepository userRepository;
	
	public UserDetailsServiceImpl(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	// 从数据库中查询用户信息
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 1. 从数据库中查询用户信息
		UserEntity userEntity = userRepository.getUserEntityByUsername(username);
		AssertUtils.notNull(userEntity, "用户不存在");
		// 2. 将用户信息封装成UserAggregate对象
		UserAggregate userAggregate = UserAggregate.builder()
				.id(userEntity.getId())
				.username(userEntity.getUsername())
				.password(userEntity.getPassword())
				.roleName(userRepository.queryRoleByName(username))
				.permissions(userRepository.queryAuthoritiesByName(username))
				.build();
		// 3. 将UserAggregate对象封装成LoginUser对象
		LoginUser loginUser = LoginUser.builder()
				.userAggregate(userAggregate)
				.accountNonLocked(!userEntity.getLocked())
				.accountNonExpired(!userEntity.getRemoved())
				.build();
		log.debug("load UserDetails(LoginUser): {}", loginUser);
		return loginUser;
	}
	
}
