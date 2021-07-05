package com.theodo.plugin.serverless.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import static com.theodo.plugin.serverless.utils.LambdaHelper.isCallingStep;

public class SlsStepsNavigationHandler implements GotoDeclarationHandler {
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        final IElementType elementType = PsiUtilCore.getElementType(sourceElement);
        if (elementType == YAMLTokenTypes.TEXT || elementType == YAMLTokenTypes.SCALAR_STRING) {
            YAMLKeyValue keyValue = PsiTreeUtil.getParentOfType(sourceElement, YAMLKeyValue.class);
            if (keyValue == null) return null;
            if (isCallingStep(keyValue)) {
                YAMLKeyValue qualifiedKeyInFile = YAMLUtil.getQualifiedKeyInFile((YAMLFile) sourceElement.getContainingFile(), "States", sourceElement.getText());
                if (qualifiedKeyInFile != null) return new PsiElement[]{qualifiedKeyInFile};
            }
        }
        return null;
    }
}
