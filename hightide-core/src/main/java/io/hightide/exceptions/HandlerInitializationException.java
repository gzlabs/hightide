package io.hightide.exceptions;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class HandlerInitializationException extends RuntimeException {

    public HandlerInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
