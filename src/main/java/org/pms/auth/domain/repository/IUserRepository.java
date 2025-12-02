package org.pms.auth.domain.repository;

import org.pms.auth.domain.model.entity.UserEntity;

import java.util.List;

/**
 * @author alcsyooterranf
 */
public interface IUserRepository {

    UserEntity getUserEntityByUsername(String username);

    List<String> queryAuthoritiesByName(String name);

    String queryRoleByName(String username);

}
