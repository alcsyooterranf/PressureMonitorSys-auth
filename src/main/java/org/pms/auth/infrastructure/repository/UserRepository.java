package org.pms.auth.infrastructure.repository;

import lombok.extern.slf4j.Slf4j;
import org.pms.auth.domain.model.entity.UserEntity;
import org.pms.auth.domain.repository.IUserRepository;
import org.pms.auth.infrastructure.adapter.AuthConverter;
import org.pms.auth.infrastructure.mapper.IUserMapper;
import org.pms.auth.infrastructure.mapper.po.UserPO;
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
