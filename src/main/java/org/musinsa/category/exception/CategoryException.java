package org.musinsa.category.exception;

import java.text.MessageFormat;
import lombok.NoArgsConstructor;
import org.musinsa.category.exception.enums.ExceptionMessage;

@NoArgsConstructor
public class CategoryException extends RuntimeException {

    private String title;

    public CategoryException(ExceptionMessage message) {
        super(message.getMessage());
    }

    public CategoryException(ExceptionMessage message, Object... arguments) {
        super(MessageFormat.format(message.getMessage(), arguments));
    }

    public CategoryException(ExceptionMessage message, Throwable cause) {
        super(message.getMessage(), cause);
    }

    public CategoryException(ExceptionMessage message, String title) {
        super(message.getMessage());
        this.title = title;
    }
}
