package org.pms.auth.infrastructure.adapter;

import org.pms.auth.domain.model.entity.UserEntity;
import org.pms.auth.infrastructure.mapper.po.UserPO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

/**
 * @author alcsyooterranf
 */
@Mapper(componentModel = "spring")
public abstract class AuthConverter {

    /**
     * 单个JavaBean转换 UserPO -> UserEntity
     *
     * @param userPO PO类
     * @return Entity类
     */
    public abstract UserEntity userPO2entity(UserPO userPO);

    /**
     * 单个JavaBean转换 UserEntity -> UserPO
     *
     * @param userEntity Entity类
     * @return PO类
     */
    @InheritInverseConfiguration(name = "userPO2entity")
    public abstract UserPO userEntity2po(UserEntity userEntity);

}
