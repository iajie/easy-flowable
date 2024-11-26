package com.superb.core.domain.model;

import com.superb.core.domain.enums.SuperbCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * {@link com.superb.core.domain.model}
 * @since 1.0  2024-09-27-14:20
 * @author MoJie
 */
@Data
public class Result<T> implements Serializable {

    /** 是否成功 */
    private boolean success = true;

    /** 接口返回描述信息 */
    private String message = "";

    /** 返回的响应码(服务端自定义) */
    private Integer code = 0;

    /** 具体的返回结果 */
    private T result;

    /**
     * 成功消息
     * @return {@link Result} {@link T}
     * @author MoJie
     */
    public static<T> Result<T> success() {
        Result<T> r = new Result<>();
        r.setMessage("成功！");
        r.setSuccess(true);
        r.setCode(200);
        return r;
    }

    /**
     * 成功，设置消息
     * @param message 消息
     * @return {@link Result} {@link T}
     * @author MoJie
     */
    public static<T> Result<T> success(String message) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setCode(200);
        r.setResult(null);
        r.setMessage(message);
        return r;
    }

    /**
     * 成功返回
     * @param data 数据
     * @return {@link Result} {@link T}
     * @author MoJie
     */
    public static<T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMessage("成功！");
        r.setResult(data);
        return r;
    }

    /**
     * 成功
     * @param message 消息
     * @param data 数据
     * @return {@link Result} {@link T}
     * @author MoJie
     */
    public static<T> Result<T> success(String message, T data) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMessage(message);
        r.setResult(data);
        return r;
    }

    /**
     * 错误信息返回
     * @return {@link Result} {@link T}
     * @author MoJie
     */
    public static<T> Result<T> error() {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setCode(500);
        r.setMessage("失败！");
        return r;
    }

    /**
     * 错误自定义消息
     * @param message 消息
     * @return {@link Result} {@link T}
     * @author MoJie
     */
    public static<T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setCode(500);
        r.setResult(null);
        r.setMessage(message);
        return r;
    }

    /**
     * 错误并返回消息和数据
     * @param message 消息
     * @param data 数据
     * @return {@link Result} {@link T}
     * @author MoJie
     */
    public static<T> Result<T> error(String message, T data) {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setCode(500);
        r.setMessage(message);
        r.setResult(data);
        return r;
    }

}
