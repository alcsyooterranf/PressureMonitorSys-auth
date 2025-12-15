package org.pms.core.infrastructure.repository;

import lombok.extern.slf4j.Slf4j;
import org.pms.core.domain.model.entity.UserEntity;
import org.pms.core.domain.repository.IUserRepository;
import org.pms.core.infrastructure.adapter.AuthConverter;
import org.pms.core.infrastructure.mapper.IUserMapper;
import org.pms.core.infrastructure.mapper.po.UserPO;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author alcsyooterranf
 */
@Slf4j
@Repository
public class UserRepository implements IUserRepository {

    private final IUserMapper userMapper;
    private final AuthConverter authConverter;

    public UserRepository(IUserMapper userMapper, AuthConverter authConverter) {
        this.userMapper = userMapper;
        this.authConverter = authConverter;
    }

    @Override
    public UserEntity getUserEntityByUsername(String username) {
        UserPO userpo = userMapper.selectUserByName(username);
        return authConverter.userPO2entity(userpo);
    }

    @Override
    public List<String> queryAuthoritiesByName(String name) {
        return userMapper.selectAuthoritiesByName(name);
    }

    @Override
    public String queryRoleByName(String username) {
        return userMapper.selectRoleByName(username);
    }

}
