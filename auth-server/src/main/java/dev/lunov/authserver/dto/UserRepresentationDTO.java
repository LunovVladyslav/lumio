package dev.lunov.authserver.dto;

public record UserRepresentationDTO(
				String id,
				String username,
				String firstName,
				String lastName,
				String email,
				ErrorDTO error
) {
}
