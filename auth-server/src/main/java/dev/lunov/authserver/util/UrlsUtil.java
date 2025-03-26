package dev.lunov.authserver.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlsUtil {

		@Value("${keycloak.host}")
		private String host;

		@Value("${keycloak.realm}")
		private String realm;


		public String getTokenUrl() {
				return "%s/realms/%s/protocol/openid-connect/token".formatted(host, realm);
		}

		public String getUsersUrl() {
				return "%s/admin/realms/%s/users".formatted(host, realm);
		}
}
