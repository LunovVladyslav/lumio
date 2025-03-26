package dev.lunov.authserver.service;

import dev.lunov.authserver.dto.LoginRequestDTO;
import dev.lunov.authserver.dto.RegistrationRequestDTO;
import dev.lunov.authserver.dto.TokenResponseDTO;
import dev.lunov.authserver.dto.UserRepresentationDTO;

public interface AuthService {

		TokenResponseDTO login(LoginRequestDTO loginRequestDTO);
		UserRepresentationDTO register(RegistrationRequestDTO registrationRequestDTO);
//		UserRepresentationDTO findUserByEmail(String email, String accessToken);
}
