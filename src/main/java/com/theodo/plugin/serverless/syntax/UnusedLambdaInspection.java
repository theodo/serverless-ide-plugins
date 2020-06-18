package com.theodo.plugin.serverless.syntax;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YamlPsiElementVisitor;

import static com.theodo.plugin.serverless.navigation.SlsFunctionNavigationHandler.isLambda;
import static com.theodo.plugin.serverless.navigation.SlsFunctionNavigationHandler.findMatchingPsiFile;

public class UnusedLambdaInspection extends LocalInspectionTool {

    public @Nullable String getStaticDescription() {
        return "Highlight Lambda with undefined code";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new YamlPsiElementVisitor() {
            @Override
            public void visitKeyValue(@NotNull YAMLKeyValue keyValue) {
                if (!"handler".equals(keyValue.getKeyText())) {
                    return;
                }
                if (!isLambda(keyValue)) return;

                String text = keyValue.getValueText();
                String[] fileAndMethod = text.split("\\.");

                if (fileAndMethod.length >= 1) {
                    PsiFile containingFile = keyValue.getContainingFile();
                    VirtualFile virtualFile = containingFile.getVirtualFile();
                    VirtualFile directory = virtualFile.getParent();

                    PsiElement codeFile = findMatchingPsiFile(keyValue, fileAndMethod[0] + ".ts", directory);
                    if (codeFile != null) return;

                    codeFile = findMatchingPsiFile(keyValue, fileAndMethod[0] + ".js", directory);
                    if (codeFile != null) return;

                    codeFile = findMatchingPsiFile(keyValue, fileAndMethod[0] + ".py", directory);
                    if (codeFile != null) return;

                    if (keyValue.getValue() != null) {
                        holder.registerProblem(keyValue.getValue(), "Lambda Code File not found");
                    }
                }
            }
        };
    }
}
