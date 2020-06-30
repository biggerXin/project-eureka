package com.picchealth.inside.vo;


import java.io.Serializable;

public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private int status;
    private String statusText;
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiResponse() {
    }

    public ApiResponse auth(){
        this.status = -403;
        this.statusText = "请求被拒绝，身份认证失败!";
        return this;
    }

    public ApiResponse exception(){
        this.status = -500;
        this.statusText = "请求异常!";
        return this;
    }
}

