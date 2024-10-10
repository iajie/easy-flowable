package com.superb.easyflowable.ui.model;

import com.superb.easyflowable.core.enums.SuperbCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @package: {@link com.superb.easyflowable.ui.model}
 * @Date: 2024-09-27-14:20
 * @Description:
 * @Author: MoJie
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

    public static<T> Result<T> success() {
        Result<T> r = new Result<>();
        r.setMessage("成功！");
        r.setSuccess(true);
        r.setCode(200);
        return r;
    }

    public static<T> Result<T> success(String message) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setCode(200);
        r.setResult(null);
        r.setMessage(message);
        return r;
    }

    public static<T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMessage("成功！");
        r.setResult(data);
        return r;
    }

    public static<T> Result<T> success(String message, T data) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMessage(message);
        r.setResult(data);
        return r;
    }

    public static<T> Result<T> error() {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setCode(500);
        r.setMessage("失败！");
        return r;
    }

    public static<T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setCode(500);
        r.setResult(null);
        r.setMessage(message);
        return r;
    }

    public static<T> Result<T> error(SuperbCodeEnum code) {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setCode(code.getCode());
        r.setResult(null);
        r.setMessage(code.getMessage());
        return r;
    }

    public static<T> Result<T> error(int code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        r.setSuccess(false);
        return r;
    }

    public static<T> Result<T> error(SuperbCodeEnum code, T data) {
        Result<T> r = new Result<>();
        r.setCode(code.getCode());
        r.setResult(data);
        r.setMessage(code.getMessage());
        r.setSuccess(false);
        return r;
    }

    public static<T> Result<T> error(String message, T data) {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setCode(500);
        r.setMessage(message);
        r.setResult(data);
        return r;
    }

}
