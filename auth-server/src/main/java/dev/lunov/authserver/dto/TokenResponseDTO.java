package dev.lunov.authserver.dto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TokenResponseDTO(
				String accessToken,
				String expiresIn,
				String refreshExpiresIn,
				String tokenType,
				String refreshToken,
				String scope
) {
}
