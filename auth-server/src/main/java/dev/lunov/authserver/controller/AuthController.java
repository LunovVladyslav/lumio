package dev.lunov.authserver.controller;

import dev.lunov.authserver.dto.LogoutDTO;
import dev.lunov.authserver.dto.RegistrationRequestDTO;
import dev.lunov.authserver.dto.TokenResponseDTO;
import dev.lunov.authserver.dto.UserRepresentationDTO;
import dev.lunov.authserver.service.AuthService;
import dev.lunov.authserver.util.StringParserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

		private final AuthService authService;
		private final StringParserUtil stringParser;

		@PostMapping("/registration")
		public UserRepresentationDTO registration(@RequestBody RegistrationRequestDTO registrationRequestDTO) {
				return authService.register(registrationRequestDTO);
		}

		@PostMapping("/login")
		public TokenResponseDTO login(@RequestHeader("Authorization") String authorizationHeader) {
				return authService.login(stringParser.getUserCredentials(authorizationHeader));
		}

		@PostMapping("/refresh")
		public TokenResponseDTO refresh(@RequestHeader("Authorization") String authorizationHeader) {
				return authService.refreshToken(stringParser.getToken(authorizationHeader));
		}

		@PostMapping("/logout")
		public ResponseEntity<Void> logout(@RequestBody LogoutDTO logoutDTO) {
				return authService.logout(logoutDTO);
		}

		@DeleteMapping("/delete/{userId}")
		public ResponseEntity<Void> delete(@RequestBody LogoutDTO logoutDTO, @PathVariable String userId) {
				return authService.deleteUser(logoutDTO, userId);
		}
}
