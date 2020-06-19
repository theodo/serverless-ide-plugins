package com.theodo.plugin.serverless.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLSequence;
import org.jetbrains.yaml.psi.YAMLSequenceItem;
import org.jetbrains.yaml.psi.YAMLValue;

import java.util.List;

import static com.theodo.plugin.serverless.utils.RefHelper.searchReference;

/**
 * NAVIGATE from 'Fn::GetAtt [ value, 'ARN'] TO value definition found in any YAML file
 */
public class GotoReferenceDefinition implements GotoDeclarationHandler {

    private static final String FN_GET_ATT = "Fn::GetAtt";

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        final IElementType elementType = PsiUtilCore.getElementType(sourceElement);
        if (elementType == YAMLTokenTypes.TEXT || elementType == YAMLTokenTypes.SCALAR_STRING) {
            YAMLKeyValue parent = PsiTreeUtil.getParentOfType(sourceElement, YAMLKeyValue.class);
            if (parent == null) return null;

            if (!FN_GET_ATT.equals(parent.getKeyText())) {
                return null;
            }

            YAMLValue value = parent.getValue();
            if (!(value instanceof YAMLSequence)) {
                return null;
            }

            YAMLSequence sequence = (YAMLSequence) value;
            List<YAMLSequenceItem> items = sequence.getItems();
            if (items.isEmpty()) return null;

            return searchReference(sourceElement);
        }

        return null;
    }

}