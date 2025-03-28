package dev.lunov.authserver.controller;

import dev.lunov.authserver.dto.LogoutDTO;
import dev.lunov.authserver.dto.RegistrationRequestDTO;
import dev.lunov.authserver.dto.TokenResponseDTO;
import dev.lunov.authserver.dto.UserRepresentationDTO;
import dev.lunov.authserver.service.AuthService;
import dev.lunov.authserver.util.StringParserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
		public TokenResponseDTO login(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
				return authService.login(stringParser.getUserCredentials(authorizationHeader));
		}

		@PostMapping("/refresh")
		public TokenResponseDTO refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken) {
				return authService.refreshToken(stringParser.getToken(refreshToken));
		}

		@PostMapping("/logout/{userId}")
		public ResponseEntity<Void> logout(@PathVariable String userId) {
				return authService.logout(userId);
		}

		@DeleteMapping("/delete/{userId}")
		public ResponseEntity<Void> delete(@PathVariable String userId, @RequestBody LogoutDTO logoutDTO) {
				return authService.deleteUser(userId, logoutDTO);
		}
}
