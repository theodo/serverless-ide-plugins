package com.theodo.plugin.serverless.syntax;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YamlPsiElementVisitor;
import org.jetbrains.yaml.psi.YamlRecursivePsiElementVisitor;

import static com.theodo.plugin.serverless.utils.LambdaHelper.isCallingStep;

public class UnusedStepInspection extends LocalInspectionTool {

    public @Nullable String getStaticDescription() {
        return "Highlight Undefined Steps";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new YamlPsiElementVisitor() {
            @Override
            public void visitKeyValue(@NotNull YAMLKeyValue keyValue) {
                YAMLKeyValue parent = PsiTreeUtil.getParentOfType(keyValue, YAMLKeyValue.class);
                if(parent == null) return;

                if("States".equals(parent.getKeyText())){
                    String stepName = keyValue.getKeyText();
                    PsiFile containingFile = keyValue.getContainingFile();

                    UsageDetection visitor = new UsageDetection(stepName);
                    containingFile.acceptChildren(visitor);
                    if(!visitor.foundUsage){
                        if (keyValue.getKey() != null) {
                            holder.registerProblem(keyValue.getKey(), "Step not used in this file");
                        }
                    }
                }
            }
        };
    }

    private static class UsageDetection extends YamlRecursivePsiElementVisitor {
        private final String stepName;
        private boolean foundUsage;

        public UsageDetection(String stepName) {
            this.stepName = stepName;
            this.foundUsage = false;
        }

        @Override
        public void visitKeyValue(@NotNull YAMLKeyValue keyValue) {
            if(isCallingStep(keyValue.getKeyText())){
                if(keyValue.getValueText().equals(stepName)){
                    foundUsage = true;
                }
            }
            super.visitKeyValue(keyValue);
        }
    }
}
