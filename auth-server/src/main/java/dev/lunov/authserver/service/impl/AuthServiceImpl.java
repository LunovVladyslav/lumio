package dev.lunov.authserver.service.impl;

import dev.lunov.authserver.dto.*;
import dev.lunov.authserver.service.AuthService;
import dev.lunov.authserver.util.StringParserUtil;
import dev.lunov.authserver.util.UrlsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

		private static final String BEARER = "Bearer %s";
		private final RestClient restClient = RestClient.create();

		private final UrlsUtil urlsUtil;
		private final StringParserUtil stringParserUtil;

		@Value("${keycloak.client-id}")
		private String clientId;
		@Value("${admin.username}")
		private String adminUsername;
		@Value("${admin.password}")
		private String adminPassword;

		@Override
		public ResponseEntity<Void> deleteUser(LogoutDTO logoutDTO, String userId) {
				this.logout(logoutDTO);

				var tokenResponse = this.loginAsAdmin();
				return restClient.delete()
								.uri(urlsUtil.deleteUserUrl(userId))
								.header(HttpHeaders.AUTHORIZATION, BEARER.formatted(tokenResponse.accessToken()))
								.retrieve()
								.toBodilessEntity();
		}

		@Override
		public ResponseEntity<Void> logout(LogoutDTO logoutDTO) {
				return restClient.post()
								.uri(urlsUtil.getLogoutUrl())
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
								.header(HttpHeaders.AUTHORIZATION, BEARER.formatted(logoutDTO.accessToken()))
								.body("client_id=%s>&refresh_token=%s".formatted(clientId, logoutDTO.refreshToken()))
								.retrieve()
								.toBodilessEntity();
		}

		@Override
		public TokenResponseDTO refreshToken(String refreshToken) {
				ResponseEntity<TokenResponseDTO> response = restClient.post()
								.uri(urlsUtil.getTokenUrl())
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
								.body("client_id=%s&refresh_token=%s&grant_type=refresh_token".formatted(clientId, refreshToken))
								.retrieve()
								.toEntity(TokenResponseDTO.class);

				return response.getBody();
		}

		// TODO: add catch exceptions functionality
		@Override
		public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
				ResponseEntity<TokenResponseDTO> response = restClient.post()
								.uri(urlsUtil.getTokenUrl())
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
								.body("client_id=%s&username=%s&password=%s&grant_type=password"
												.formatted(
																clientId, loginRequestDTO.username(), loginRequestDTO.password()
												))
								.retrieve()
								.toEntity(TokenResponseDTO.class);

				return response.getBody();
		}

		// TODO: add email verification functionality
		@Override
		public UserRepresentationDTO register(RegistrationRequestDTO registrationRequestDTO) {
				CreateUserForm userRequest = new CreateUserForm(
								registrationRequestDTO.username(),
								registrationRequestDTO.firstName(),
								registrationRequestDTO.lastName(),
								registrationRequestDTO.email(),
								true,
								true,
								List.of(
												new CredentialRepresentationDTO(
																"password",
																registrationRequestDTO.password(),
																false
												)
								)
				);

				var tokenResponse = loginAsAdmin();
				AtomicReference<ErrorDTO> errMsg = new AtomicReference<>();

				restClient.post()
								.uri(urlsUtil.getUsersUrl())
								.header(HttpHeaders.AUTHORIZATION, BEARER.formatted(tokenResponse.accessToken()))
								.body(userRequest)
								.retrieve()
								.onStatus(err -> {
										if (err.getStatusCode() != HttpStatus.CREATED) {
												var message = stringParserUtil.getMessageFromBody(err.getBody());
												errMsg.set(new ErrorDTO(
																err.getStatusCode().value(),
																err.getStatusText(),
																message
												));
										}
										return true;
								})
								.toBodilessEntity();

				if (errMsg.get() != null) {
						return registrationFailed(errMsg.get());
				}

				return findUserByEmail(registrationRequestDTO.email(), tokenResponse.accessToken());
		}

		public UserRepresentationDTO findUserByEmail(String email, String accessToken) {
				var userList = restClient.get()
								.uri("%s?email=%s".formatted(urlsUtil.getUsersUrl(), email))
								.header(HttpHeaders.AUTHORIZATION, BEARER.formatted(accessToken))
								.retrieve()
								.body(new ParameterizedTypeReference<List<UserRepresentationDTO>>() {});

				if (userList == null || userList.isEmpty()) {
						log.warn("No user found with email: {}", email);
						return null;
				}

				UserRepresentationDTO user = userList.getFirst();
				log.info(user.toString());

				return user;
		}

		private TokenResponseDTO loginAsAdmin() {
				return login(new LoginRequestDTO(adminUsername, adminPassword));
		}

		private UserRepresentationDTO registrationFailed(ErrorDTO errorDTO) {
				return new UserRepresentationDTO("0", "none","none","none","none",errorDTO);
		}
}
