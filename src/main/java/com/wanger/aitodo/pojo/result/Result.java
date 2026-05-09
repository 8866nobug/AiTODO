package com.wanger.aitodo.pojo.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "统一响应结果封装")
public class Result<T> implements Serializable {

    @Schema(description = "业务状态码: 200-成功, 500-失败")
    private Integer code;

    @Schema(description = "提示信息")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    // 私有化构造器，强制通过静态方法创建
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应 - 携带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功响应 - 不携带数据
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 失败响应 - 自定义信息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 失败响应 - 自定义状态码和信息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
