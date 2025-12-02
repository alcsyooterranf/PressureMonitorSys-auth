package org.pms.auth.domain.repository;

import org.pms.auth.domain.model.entity.LoginUser;
import org.pms.auth.domain.model.valobj.UserTokenVO;

/**
 * @author alcsyooterranf
 */
public interface IAuthRepository {

    void saveUserToken(LoginUser user);

    void updateUserToken(UserTokenVO tokenVO);

}
