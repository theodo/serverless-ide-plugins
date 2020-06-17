package com.theodo.plugin.serverless.syntax;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLSequenceItem;
import org.jetbrains.yaml.psi.YAMLValue;
import org.jetbrains.yaml.psi.YamlPsiElementVisitor;
import org.jetbrains.yaml.psi.impl.YAMLArrayImpl;

import java.util.List;

import static com.theodo.plugin.serverless.navigation.SlsArnRefNavigationHandler.searchLambdaInFiles;

public class UnknownLambdaInspection extends LocalInspectionTool {

    public @Nullable String getStaticDescription() {
        return "Highlight Unknown Lambda";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new YamlPsiElementVisitor() {
            @Override
            public void visitKeyValue(@NotNull YAMLKeyValue keyValue) {
                if (!"Fn::GetAtt".equals(keyValue.getKeyText())) {
                    return;
                }
                YAMLValue value = keyValue.getValue();
                if (!(value instanceof YAMLArrayImpl)) {
                    return;
                }

                YAMLArrayImpl sequence = (YAMLArrayImpl) value;
                List<YAMLSequenceItem> items = sequence.getItems();
                if (items.isEmpty()) return;

                YAMLSequenceItem element = items.get(0);
                if(element == null) return;

                PsiElement[] psiElements = searchLambdaInFiles(element.getValue());
                if (psiElements == null || psiElements.length == 0) {
                    holder.registerProblem(keyValue, "Can't find lambda definition");
                }

            }
        };
    }

}
