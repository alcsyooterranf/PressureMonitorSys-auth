package org.pms.core.infrastructure.repository;

import org.pms.core.domain.model.entity.LoginUser;
import org.pms.core.domain.model.valobj.UserTokenVO;
import org.pms.core.domain.repository.IAuthRepository;
import org.pms.core.infrastructure.utils.JwtUtil;
import org.pms.core.infrastructure.utils.TokenUtil;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author alcsyooterranf
 */
@Repository
public class AuthRepository implements IAuthRepository {

    private final TokenUtil tokenUtil;

    public AuthRepository(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Override
    public void saveUserToken(LoginUser user) {
        // 1.生成UUID
        String accessTokenUUID = UUID.randomUUID().toString();
        String refreshTokenUUID = UUID.randomUUID().toString();
        // 2.生成token
        String accessToken = tokenUtil.genAccessToken(user, accessTokenUUID);
        String refreshToken = tokenUtil.genRefreshToken(user, refreshTokenUUID);
        // 3.仅将refreshToken存入redis
        tokenUtil.saveRefreshToken(refreshTokenUUID, refreshToken);
        // 4.存入loginUser, 返回给前端
        UserTokenVO tokenVO = UserTokenVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .publicKey64(JwtUtil.getPublicKeyStr())
                .build();
        user.setTokenVO(tokenVO);
    }

    @Override
    public void updateUserToken(UserTokenVO tokenVO) {
        tokenUtil.refreshToken(tokenVO);
    }

}
