package dev.lunov.authserver.dto;

import java.util.List;

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
