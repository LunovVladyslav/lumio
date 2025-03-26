package dev.lunov.authserver.controller;

import dev.lunov.authserver.dto.LoginRequestDTO;
import dev.lunov.authserver.dto.RegistrationRequestDTO;
import dev.lunov.authserver.dto.TokenResponseDTO;
import dev.lunov.authserver.dto.UserRepresentationDTO;
import dev.lunov.authserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

		private final AuthService authService;

		@PostMapping("/registration")
		public UserRepresentationDTO registration(@RequestBody RegistrationRequestDTO registrationRequestDTO) {
				return authService.register(registrationRequestDTO);
		}

		@PostMapping("/login")
		public TokenResponseDTO login(Authentication authentication) {
				return authService.login(
								new LoginRequestDTO(authentication.getName(), authentication.getCredentials().toString())
				);
		}
}
