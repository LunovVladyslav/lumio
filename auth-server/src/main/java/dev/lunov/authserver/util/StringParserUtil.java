package dev.lunov.authserver.util;

import dev.lunov.authserver.dto.LoginRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class StringParserUtil {

		public String getMessageFromBody(InputStream inputStream) throws IOException {
				var body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

				int firstIndex = body.indexOf(":\"");
				int lastIndex = body.lastIndexOf("\"}");

				return body.substring(firstIndex + 2, lastIndex);
		}

		public LoginRequestDTO getUserCredentials(String header) {

						String base64Credentials = header.substring("Basic".length()).trim();
						byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
						String credentials = new String(credDecoded, StandardCharsets.UTF_8);

						// credentials = username:password
						final String[] values = credentials.split(":", 2);
						return new LoginRequestDTO(values[0], values[1]);
		}

		public String getToken(String authorizationHeader) {
				if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
						throw new IllegalArgumentException("Invalid Authorization header");
				}
				var token = authorizationHeader.substring("Bearer".length()).trim();
				log.debug("token: {}", token);
				return token;
		}
}
