package com.theodo.plugin.serverless.syntax;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YamlPsiElementVisitor;

import static com.theodo.plugin.serverless.utils.LambdaHelper.isCallingStep;

public class UnknownStepInspection extends LocalInspectionTool {

    public @Nullable String getStaticDescription() {
        return "Highlight Undefined Steps";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new YamlPsiElementVisitor() {
            @Override
            public void visitKeyValue(@NotNull YAMLKeyValue keyValue) {
                if (isCallingStep(keyValue)) {
                    String targetStepName = keyValue.getValueText();
                    YAMLKeyValue qualifiedKeyInFile = YAMLUtil.getQualifiedKeyInFile((YAMLFile) keyValue.getContainingFile(), "States", targetStepName);
                    if(qualifiedKeyInFile == null){
                        holder.registerProblem(keyValue, "Can't find the Step definition in this file");
                    }
                }
            }
        };
    }

}
