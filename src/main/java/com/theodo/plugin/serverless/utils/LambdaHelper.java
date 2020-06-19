package com.theodo.plugin.serverless.utils;

public class LambdaHelper {
    public static boolean isCallingStep(String stepFunctionTag){
        return ("Next".equals(stepFunctionTag) || "Default".equals(stepFunctionTag) || "StartAt".equals(stepFunctionTag));
    }
}
