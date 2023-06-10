package com.example.zodeaccount.common.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

@Data
public class Result extends HashMap<String, Object> implements Serializable{
    private static final long serialVersionUID = 6340845126873415079L;

    public static final int SUCCESS = 20000;
    public static final int ERROR = 20001;
    public static final Serializable EMPTY = new Serializable() {
    };

    public Result() {
    }

    public static Result me() {
        return new Result();
    }

    public Result setMessage(String message) {
        this.put((String)"message", message);
        return this;
    }

    public Result setSuccess(boolean flag) {
        this.put((String)"success", flag);
        return this;
    }

    public Result setData(Object data) {
        this.put("data", data);
        return this;
    }

    public Object getData() {
        return this.get("data");
    }

    public boolean isSuccess() {
        return (Boolean)this.get("success");
    }

    public Result setState(int state) {
        this.put((String)"state", state);
        return this;
    }

    public Result success(Object data) {
        return this.setSuccess(true).setMessage("成功").setState(20000).setData(data == null ? EMPTY : data);
    }

    public Result success() {
        return this.success((Object)null);
    }

    public Result error(Object data) {
        return this.setSuccess(false).setMessage("失败").setState(20001).setData(data == null ? EMPTY : data);
    }

    public Result error() {
        return this.error((Object)null);
    }

    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
