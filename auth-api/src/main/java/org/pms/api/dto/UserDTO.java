package org.pms.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author alcsyooterranf
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class UserDTO implements Serializable {
	
	private Long id;
	private String username;
	private transient String password;
	private String roleName;
	private List<String> permissions;
	
}

