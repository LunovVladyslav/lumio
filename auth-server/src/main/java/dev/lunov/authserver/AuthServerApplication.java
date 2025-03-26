package dev.lunov.authserver;

import com.google.common.io.ByteStreams;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
public class AuthServerApplication {

		public static void main(String[] args) {
				SpringApplication.run(AuthServerApplication.class, args);
		}

		@Bean
		CommandLineRunner init() {

				return args -> {
						AtomicReference<Error> errMsg = new AtomicReference<>();

						RestClient restClient = RestClient.create();

						ResponseEntity<TokenResponse> response = restClient.post()
										.uri("http://localhost:8080/realms/server/protocol/openid-connect/token")
										.header("Content-Type", "application/x-www-form-urlencoded")
										.body("client_id=web-app&username=myuser&password=password&grant_type=password")
										.retrieve()
										.toEntity(TokenResponse.class);

						var tokenResponse = response.getBody();

						System.out.println(tokenResponse);

						RegisterUserRequest userRequest = new RegisterUserRequest(
										"vlunov",
										"Vlad",
										"Lunov",
										"lunov@vlunov.com",
										true,
										true,
										List.of(
														new CredentialRepresentation(
																		"password",
																		"password",
																		false
														)
										)
						);

						restClient.post()
										.uri("http://localhost:8080/admin/realms/server/users")
										.header("Authorization", "Bearer %s".formatted(tokenResponse.access_token()))
										.body(userRequest)
										.retrieve()
										.onStatus(err -> {
												if (err.getStatusCode() != HttpStatus.CREATED) {
														var body = new String(ByteStreams.toByteArray(err.getBody()), StandardCharsets.UTF_8);

														int firstIndex = body.indexOf(":\"");
														int lastIndex = body.lastIndexOf("\"}");

														var message = body.substring(firstIndex + 2, lastIndex);
														errMsg.set(
																		new Error(
																						err.getStatusCode().value(),
																						err.getStatusText(),
																						message
																		)
														);
												}
												return true;
										})
										.toBodilessEntity();

						if (errMsg.get() != null) {
								System.out.println(errMsg.get());
								return;
						}

						var userList = restClient.get()
										.uri("http://localhost:8080/admin/realms/server/users?email=%s".formatted(userRequest.email()))
										.header("Authorization", "Bearer %s".formatted(tokenResponse.access_token()))
										.retrieve()
										.toEntity(List.class)
										.getBody();

						System.out.println(userList.getFirst());

				};


		}

}

record TokenResponse(
				String access_token,
				String expires_in,
				String refresh_expires_in,
				String token_type,
				String refresh_token,
				String scope
) {
}

record UserRepresentation(
				String id,
				String username,
				String firstName,
				String lastName,
				String email
) {}

record RegisterUserRequest(
				String username,
				String firstName,
				String lastName,
				String email,
				boolean emailVerified,
				boolean enabled,
				List<CredentialRepresentation> credentials
				) {
}

record CredentialRepresentation(
				String type,
				String value,
				boolean temporary
				) {
}

record Error(
				int status,
				String errorMessage,
				String text
) {}