package com.akramhossain.quranulkarim.model;

public class JuristicMethod {

    private  int methodId;
    private String methodName;

    public JuristicMethod(int methodId, String methodName) {
        this.methodId = methodId;
        this.methodName = methodName;
    }

    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString () {
        return methodName;
    }

}
