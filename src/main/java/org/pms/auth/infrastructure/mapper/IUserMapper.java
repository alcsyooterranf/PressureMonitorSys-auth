package org.pms.auth.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pms.auth.infrastructure.mapper.po.UserPO;

import java.util.List;

/**
 * @author alcsyooterranf
 */
@Mapper
public interface IUserMapper {

    List<String> selectAuthoritiesByName(@Param("name") String name);

    String selectRoleByName(@Param("username") String username);

    UserPO selectUserByName(@Param("username") String username);

}
