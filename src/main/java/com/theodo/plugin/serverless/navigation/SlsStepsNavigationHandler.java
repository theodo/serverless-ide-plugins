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

import java.util.regex.Pattern;

/**
 * NAVIGATE from '${self:something} to something DEFINITION
 */
public class SlsStepsNavigationHandler implements GotoDeclarationHandler {
    private static final Pattern REF_PATTERN = Pattern.compile("(?:\\$\\{self:([^$^}]*))");

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        final IElementType elementType = PsiUtilCore.getElementType(sourceElement);
        if (elementType == YAMLTokenTypes.TEXT || elementType == YAMLTokenTypes.SCALAR_STRING) {
            YAMLKeyValue parent = PsiTreeUtil.getParentOfType(sourceElement, YAMLKeyValue.class);
            if (parent == null) return null;
            String keyText = parent.getKeyText();
            if ("Next".equals(keyText)) {
                YAMLKeyValue qualifiedKeyInFile = YAMLUtil.getQualifiedKeyInFile((YAMLFile) sourceElement.getContainingFile(), "States", sourceElement.getText());
                if (qualifiedKeyInFile != null) return new PsiElement[]{qualifiedKeyInFile};
            }
        }
        return null;
    }
}