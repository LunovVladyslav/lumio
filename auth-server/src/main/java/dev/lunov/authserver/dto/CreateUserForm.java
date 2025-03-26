package dev.lunov.authserver.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateUserForm(
				String username,
				String firstName,
				String lastName,
				String email,
				boolean emailVerified,
				boolean enabled,
				List<CredentialRepresentationDTO> credentials
) {
}
