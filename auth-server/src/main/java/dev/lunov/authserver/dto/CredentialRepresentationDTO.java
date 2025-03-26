package dev.lunov.authserver.dto;

public record CredentialRepresentationDTO(
				String type,
				String value,
				boolean temporary
) {
}
