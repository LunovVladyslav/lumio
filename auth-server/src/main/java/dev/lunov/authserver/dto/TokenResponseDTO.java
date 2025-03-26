package dev.lunov.authserver.dto;

public record TokenResponseDTO(
				String access_token,
				String expires_in,
				String refresh_expires_in,
				String token_type,
				String refresh_token,
				String scope
) {
}
