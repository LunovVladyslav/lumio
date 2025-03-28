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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

		private static final String BEARER = "Bearer %s";
		private static final String PASSWORD = "password";

		private final RestClient restClient;
		private final UrlsUtil urlsUtil;
		private final StringParserUtil stringParserUtil;

		@Value("${keycloak-server.client.id}")
		private String clientId;
		@Value("${keycloak-server.admin.id}")
		private String adminId;
		@Value("${keycloak-server.admin.username}")
		private String adminUsername;
		@Value("${keycloak-server.admin.password}")
		private String adminPassword;

		@Override
		public ResponseEntity<Void> deleteUser(String userId) {
				this.logout(userId);
				var tokenResponse = this.loginAsAdmin();

				try {
						return restClient.delete()
										.uri(urlsUtil.deleteUserUrl(userId))
										.header(HttpHeaders.AUTHORIZATION, BEARER.formatted(tokenResponse.access_token()))
										.retrieve()
										.toBodilessEntity();
				} finally {
						this.logoutAdmin(tokenResponse.access_token());
				}
		}

		@Override
		public ResponseEntity<Void> logout(String userId) {
				var tokenResponse = this.loginAsAdmin();
				try {
						return restClient.post()
										.uri(urlsUtil.getLogoutUrl(userId))
										.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
										.header(HttpHeaders.AUTHORIZATION, BEARER.formatted(tokenResponse.access_token()))
										.retrieve()
										.toBodilessEntity();
				} finally {
						this.logoutAdmin(tokenResponse.access_token());
				}
		}

		@Override
		public TokenResponseDTO refreshToken(String refreshToken) {
				MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
				body.setAll(Map.of(
								"client_id", clientId,
								"refresh_token", refreshToken,
								"grant_type", "refresh_token"
				));

				ResponseEntity<TokenResponseDTO> response = restClient.post()
								.uri(urlsUtil.getTokenUrl())
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
								.body(body)
								.retrieve()
								.toEntity(TokenResponseDTO.class);

				log.info("Refresh token: {}", response.getBody());

				return response.getBody();
		}

		// TODO: add catch exceptions functionality
		@Override
		public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
				MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
				body.setAll(Map.of(
								"client_id", clientId,
								"username", loginRequestDTO.username(),
								"password", loginRequestDTO.password(),
								"grant_type", PASSWORD
				));

				log.info("Login request: {}", body);

				ResponseEntity<TokenResponseDTO> response = restClient.post()
								.uri(urlsUtil.getTokenUrl())
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
								.body(body)
								.retrieve()
								.toEntity(TokenResponseDTO.class);

				return response.getBody();
		}

		// TODO: add email verification functionality
		@Override
		public UserRepresentationDTO register(RegistrationRequestDTO registrationRequestDTO) {
				var tokenResponse = loginAsAdmin();

				CreateUserForm userRequest = createRequestForm(registrationRequestDTO);
				AtomicReference<ErrorDTO> errMsg = new AtomicReference<>();

				try {
						restClient.post()
										.uri(urlsUtil.getUsersUrl())
										.header(HttpHeaders.AUTHORIZATION, BEARER.formatted(tokenResponse.access_token()))
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
						return findUserByEmail(registrationRequestDTO.email(), tokenResponse.access_token());
				} finally {
						this.logoutAdmin(tokenResponse.access_token());
				}
		}

		private CreateUserForm createRequestForm(RegistrationRequestDTO registrationRequestDTO) {
				return new CreateUserForm(
								registrationRequestDTO.username(),
								registrationRequestDTO.firstName(),
								registrationRequestDTO.lastName(),
								registrationRequestDTO.email(),
								true,
								true,
								List.of(
												new CredentialRepresentationDTO(
																PASSWORD,
																registrationRequestDTO.password(),
																false
												)
								)
				);
		}

		public UserRepresentationDTO findUserByEmail(String email, String accessToken) {
				var userList = restClient.get()
								.uri("%s?email=%s".formatted(urlsUtil.getUsersUrl(), email))
								.header(HttpHeaders.AUTHORIZATION, BEARER.formatted(accessToken))
								.retrieve()
								.body(new ParameterizedTypeReference<List<UserRepresentationDTO>>() {
								});

				Objects.requireNonNull(userList);
				UserRepresentationDTO user = userList.getFirst();
				log.info(user.toString());

				return user;
		}

		private TokenResponseDTO loginAsAdmin() {
				return login(new LoginRequestDTO(adminUsername, adminPassword));
		}

		private void logoutAdmin(String accessToken) {

				restClient.post()
								.uri(urlsUtil.getLogoutUrl(adminId))
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
								.header(HttpHeaders.AUTHORIZATION, BEARER.formatted(accessToken))
								.retrieve()
								.toBodilessEntity();
		}

		private UserRepresentationDTO registrationFailed(ErrorDTO errorDTO) {
				return new UserRepresentationDTO("0", "none", "none", "none", "none", errorDTO);
		}
}
