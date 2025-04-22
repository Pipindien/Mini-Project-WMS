package com.financial_goal_service.app.utility;

import com.financial_goal_service.app.dto.restapi.RestApiError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class CoreThrowHandler extends Throwable{
    @Serial
    private static final long serialVersionUID = 1L;
    private final String message;
    private final Integer code;
    private final Map<String, Serializable> error;

    /**
     * Constructor for custom exception with static message.
     *
     * @param status  HTTP status code.
     * @param message Error message.
     * @param error   Additional error details.
     */
    public CoreThrowHandler(Integer status, String message, Map<String, Serializable> error) {
        super(message);
        this.code = status;
        this.message = message;
        this.error = error;
    }

    /**
     * Constructor to handle RestApiError with dynamic translation.
     *
     * @param restApiError Rest API error enum with the default message key.
     * @param error        Additional error details.
     * @param messageSource MessageSource for resolving localized messages.
     */
    public CoreThrowHandler(RestApiError restApiError, Map<String, Serializable> error, MessageSource messageSource) {
        super();
        this.code = restApiError.getCode();
        this.error = error;
        Locale locale = LocaleContextHolder.getLocale();
        // Fetch the translated message using the message key from the enum
        this.message = messageSource.getMessage(restApiError.getMessage(), null, restApiError.getMessage(), locale);
    }
}
