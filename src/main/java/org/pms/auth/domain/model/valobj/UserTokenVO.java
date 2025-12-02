package org.pms.auth.domain.model.valobj;

import lombok.Builder;
import lombok.Data;

/**
 * @author alcsyooterranf
 */
@Data
@Builder
public class UserTokenVO {

    private String accessToken;
    private String refreshToken;
    private String publicKey64;

}
