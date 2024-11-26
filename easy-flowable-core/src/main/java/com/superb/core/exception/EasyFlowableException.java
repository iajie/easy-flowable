package com.superb.core.exception;

/**
 * easy-flowable异常类
 * {@link com.superb.core.exception}
 * @since 1.0  2024-09-26-14:01
 * @author MoJie
 */
public class EasyFlowableException extends RuntimeException {

    /**
     * @param message 异常消息
     */
    public EasyFlowableException(String message) {
        super(message);
    }

    /**
     * @param message 消息
     * @param cause 异常
     */
    public EasyFlowableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause 异常
     */
    public EasyFlowableException(Throwable cause) {
        super(cause);
    }
}
