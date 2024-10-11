package com.superb.easyflowable.ui.exception;

import com.superb.easyflowable.core.exception.EasyFlowableException;
import com.superb.easyflowable.ui.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @package: {@link com.superb.easyflowable.ui.exception}
 * @Date: 2024-10-11-11:12
 * @Description:
 * @Author: MoJie
 */
@Slf4j
@RestControllerAdvice
public class EasyGlobalException {

    @ExceptionHandler(EasyFlowableException.class)
    public Result<String> exception(EasyFlowableException e) {
        e.printStackTrace();
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> exception(Exception e) {
        e.printStackTrace();
        return Result.error(e.getMessage());
    }

}
