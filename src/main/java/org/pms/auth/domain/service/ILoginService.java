package org.pms.auth.domain.service;

import org.pms.auth.domain.model.valobj.UserTokenVO;

/**
 * @author alcsyooterranf
 */
public interface ILoginService {

    UserTokenVO doRefresh(String refreshToken);

}
