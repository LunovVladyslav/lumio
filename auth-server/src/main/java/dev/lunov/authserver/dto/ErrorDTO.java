package dev.lunov.authserver.dto;

public record ErrorDTO(
				int status,
				String errorMessage,
				String text
) {
}
