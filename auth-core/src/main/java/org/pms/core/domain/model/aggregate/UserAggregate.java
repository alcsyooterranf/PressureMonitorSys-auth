package org.pms.core.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author alcsyooterranf
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAggregate implements Serializable {

    @Serial
    private static final long serialVersionUID = 666744866488526569L;

    private Long id;
    private String username;
    private transient String password;
    private String roleName;
    private List<String> permissions;

}
