package com.theodo.plugin.serverless.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.theodo.plugin.serverless.navigation.utils.SearchInYamlFileProcessor;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLSequenceItem;
import org.jetbrains.yaml.psi.YAMLValue;
import org.jetbrains.yaml.psi.impl.YAMLArrayImpl;

import java.util.List;

/**
 * NAVIGATE from 'Fn::GetAtt [ value, 'ARN'] TO value definition found in any YAML file
 */
public class SlsArnRefNavigationHandler implements GotoDeclarationHandler {

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        final IElementType elementType = PsiUtilCore.getElementType(sourceElement);
        if (elementType == YAMLTokenTypes.TEXT || elementType == YAMLTokenTypes.SCALAR_STRING) {
            YAMLKeyValue parent = PsiTreeUtil.getParentOfType(sourceElement, YAMLKeyValue.class);
            if (parent == null) return null;

            if (!"Fn::GetAtt".equals(parent.getKeyText())) {
                return null;
            }

            YAMLValue value = parent.getValue();
            if (!(value instanceof YAMLArrayImpl)) {
                return null;
            }

            YAMLArrayImpl sequence = (YAMLArrayImpl) value;
            List<YAMLSequenceItem> items = sequence.getItems();
            if (items.isEmpty()) return null;

            return searchLambdaInFiles(sourceElement);
        }

        return null;
    }

    public static PsiElement[] searchLambdaInFiles(PsiElement sourceElement) {
        if(sourceElement == null) return null;
        PsiSearchHelper searchHelper = PsiSearchHelper.getInstance(sourceElement.getProject());
        GlobalSearchScope allScope = GlobalSearchScope.allScope(sourceElement.getProject());
        GlobalSearchScope yamlTypeScope = GlobalSearchScope.getScopeRestrictedByFileTypes(allScope, YAMLFileType.YML);
        SearchInYamlFileProcessor processor = new SearchInYamlFileProcessor(element -> element instanceof YAMLKeyValue && ((YAMLKeyValue) element).getKeyText().equals(sourceElement.getText()));
        searchHelper.processAllFilesWithWord(sourceElement.getText(), yamlTypeScope, processor, true);
        return processor.get();
    }
}