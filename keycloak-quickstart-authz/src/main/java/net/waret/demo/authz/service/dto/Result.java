package net.waret.demo.authz.service.dto;

import net.waret.demo.authz.domain.Resource;

import lombok.Data;

@Data
public class Result {

    private boolean result;

    private String reason;

    public static Result failed(String reson) {
        return new Result().setReason(reson).setResult(false);
    }

    public static Result ok() {
        return new Result().setResult(true);
    }

    public Result setResult(boolean result) {
        this.result = result;
        return this;
    }

    public Result setReason(String reason) {
        this.reason = reason;
        return this;
    }

}
