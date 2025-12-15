package org.pms.core.domain.repository;

import org.pms.core.domain.model.entity.LoginUser;
import org.pms.core.domain.model.valobj.UserTokenVO;

/**
 * @author alcsyooterranf
 */
public interface IAuthRepository {

    void saveUserToken(LoginUser user);

    void updateUserToken(UserTokenVO tokenVO);

}
