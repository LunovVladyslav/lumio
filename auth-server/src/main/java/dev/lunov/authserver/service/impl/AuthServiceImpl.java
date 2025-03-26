package dev.lunov.authserver.service.impl;

import dev.lunov.authserver.dto.*;
import dev.lunov.authserver.service.AuthService;
import dev.lunov.authserver.util.StringParserUtil;
import dev.lunov.authserver.util.UrlsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

		private final RestClient restClient = RestClient.create();

		private final UrlsUtil urlsUtil;
		private final StringParserUtil stringParserUtil;

		@Value("${keycloak.client-id}")
		private String clientId;
		@Value("${admin.username}")
		private String adminUsername;
		@Value("${admin.password}")
		private String adminPassword;

		// TODO: add catch exceptions functionality
		@Override
		public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
				ResponseEntity<TokenResponseDTO> response = restClient.post()
								.uri(urlsUtil.getTokenUrl())
								.header("Content-Type", "application/x-www-form-urlencoded")
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
								.header("Authorization", "Bearer %s".formatted(tokenResponse.access_token()))
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
		}

		public UserRepresentationDTO findUserByEmail(String email, String accessToken) {
				var userList = restClient.get()
								.uri("%s?email=%s".formatted(urlsUtil.getUsersUrl(), email))
								.header("Authorization", "Bearer %s".formatted(accessToken))
								.retrieve()
								.body(new ParameterizedTypeReference<List<UserRepresentationDTO>>() {});

				if (userList == null || userList.isEmpty()) {
						log.warn("No user found with email: {}", email);
						return null;
				}

				UserRepresentationDTO user = userList.get(0);
				log.info(user.toString());

				return user;
//								.toEntity(List.class)
//								.getBody();
//
//				AtomicReference<UserRepresentationDTO> user = new AtomicReference<>();
//
//				userList.forEach(res -> {
//						user.set((UserRepresentationDTO) res);
//				});
//
//				log.info(user.get().toString());
//
//				return user.get();
		}

		private TokenResponseDTO loginAsAdmin() {
				return login(new LoginRequestDTO(adminUsername, adminPassword));
		}

		private UserRepresentationDTO registrationFailed(ErrorDTO errorDTO) {
				return new UserRepresentationDTO("0", "none","none","none","none",errorDTO);
		}
}
