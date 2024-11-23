package com.superb.core.exception;

/**
 * @package: {@link com.superb.core.exception}
 * @Date: 2024-09-26-14:01
 * @Description: easy-flowable异常类
 * @Author: MoJie
 */
public class EasyFlowableException extends RuntimeException {

    public EasyFlowableException(String message) {
        super(message);
    }

    public EasyFlowableException(String message, Throwable cause) {
        super(message, cause);
    }

    public EasyFlowableException(Throwable cause) {
        super(cause);
    }
}
