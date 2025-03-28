package dev.lunov.authserver.service;

import dev.lunov.authserver.dto.*;
import org.springframework.http.ResponseEntity;

public interface AuthService {

		TokenResponseDTO login(LoginRequestDTO loginRequestDTO);
		UserRepresentationDTO register(RegistrationRequestDTO registrationRequestDTO);
		TokenResponseDTO refreshToken(String token);
		ResponseEntity<Void> logout(String userId);
		ResponseEntity<Void> deleteUser(String userId);
}
