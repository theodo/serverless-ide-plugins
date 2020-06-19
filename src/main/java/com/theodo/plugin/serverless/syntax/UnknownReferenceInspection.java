package com.theodo.plugin.serverless.syntax;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.*;

import java.util.List;

import static com.theodo.plugin.serverless.utils.RefHelper.searchReference;

public class UnknownReferenceInspection extends LocalInspectionTool {

    private static final String FN_GET_ATT = "Fn::GetAtt";
    private static final String REF = "Ref";

    public @Nullable String getStaticDescription() {
        return "Highlight Unknown Reference";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new YamlPsiElementVisitor() {
            @Override
            public void visitKeyValue(@NotNull YAMLKeyValue keyValue) {
                if (FN_GET_ATT.equals(keyValue.getKeyText())) {
                    searchInSequences(keyValue.getValue());
                } else if (REF.equals(keyValue.getKeyText())) {
                    PsiElement[] psiElements = searchReference(keyValue.getValue());
                    if (psiElements == null || psiElements.length == 0) {
                       holder.registerProblem(keyValue.getValue(), "Can't find reference");
                   }
                }
            }

            private void searchInSequences(YAMLValue value) {
                if (!(value instanceof YAMLSequence)) {
                    return;
                }

                YAMLSequence sequence = (YAMLSequence) value;
                List<YAMLSequenceItem> items = sequence.getItems();
                if (items.isEmpty()) return;

                YAMLSequenceItem element = items.get(0);
                if(element == null) return;

                YAMLValue elementValue = element.getValue();
                if(elementValue == null) return;

                PsiElement[] psiElements = searchReference(elementValue);
                if (psiElements == null || psiElements.length == 0) {
                    holder.registerProblem(elementValue, "Can't find reference");
                }
            }
        };
    }

}
