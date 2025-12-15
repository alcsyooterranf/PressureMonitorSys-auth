package org.pms.core.domain.service;

import org.pms.core.domain.model.valobj.UserTokenVO;

/**
 * @author alcsyooterranf
 */
public interface ILoginService {

    UserTokenVO doRefresh(String refreshToken);

}
