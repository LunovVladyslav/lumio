package dev.lunov.authserver.dto;


public record RegistrationRequestDTO(
				String username,
				String firstName,
				String lastName,
				String email,
				String password
) {
}
