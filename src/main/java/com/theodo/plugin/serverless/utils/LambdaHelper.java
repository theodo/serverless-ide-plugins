package com.theodo.plugin.serverless.utils;

import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.yaml.psi.YAMLKeyValue;

public class LambdaHelper {
    public static boolean isCallingStep(YAMLKeyValue stepFunctionTag){
        return ("Next".equals(stepFunctionTag.getKeyText())
                || "Default".equals(stepFunctionTag.getKeyText())
                || "StartAt".equals(stepFunctionTag.getKeyText())) && inStepFunction(stepFunctionTag);
    }

    private static boolean inStepFunction(YAMLKeyValue node) {
        YAMLKeyValue parent = PsiTreeUtil.getParentOfType(node, YAMLKeyValue.class);
        if (parent == null) return false;
        if("States".equals(parent.getKeyText())) return true;
        return inStepFunction(parent);
    }
}
