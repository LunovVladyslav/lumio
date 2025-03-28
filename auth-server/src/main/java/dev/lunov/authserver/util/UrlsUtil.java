package dev.lunov.authserver.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlsUtil {

		@Value("${keycloak-server.host}")
		private String host;

		@Value("${keycloak-server.realm}")
		private String realm;


		public String getTokenUrl() {
				return "%s/realms/%s/protocol/openid-connect/token".formatted(host, realm);
		}

		public String getUsersUrl() {
				return "%s/admin/realms/%s/users".formatted(host, realm);
		}

		public String getRefreshTokenUrl() {
//				return "%s/realms/master/protocol/openid-connect/token".formatted(host);
				return "%s/realms/%s/protocol/openid-connect/token".formatted(host, realm);
		}

		public String getLogoutUrl(String userId) {
				return "%s/admin/realms/%s/users/%s/logout".formatted(host, realm, userId);
		}

		public String deleteUserUrl(String userId) {
				return "%s/admin/realms/%s/users/%s".formatted(host, realm, userId);
		}
}
