package dev.lunov.authserver.dto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ErrorDTO(
				int status,
				String errorMessage,
				String text
) {
}
