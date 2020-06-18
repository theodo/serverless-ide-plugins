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

public class SlsImportValueNavigationHandler implements GotoDeclarationHandler {

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        final IElementType elementType = PsiUtilCore.getElementType(sourceElement);
        if (elementType == YAMLTokenTypes.TEXT || elementType == YAMLTokenTypes.SCALAR_STRING) {
            YAMLKeyValue parent = PsiTreeUtil.getParentOfType(sourceElement, YAMLKeyValue.class);
            if (parent == null) return null;

            if (!"Fn::ImportValue".equals(parent.getKeyText())) {
                return null;
            }

            PsiSearchHelper searchHelper = PsiSearchHelper.getInstance(sourceElement.getProject());
            GlobalSearchScope allScope = GlobalSearchScope.allScope(sourceElement.getProject());
            GlobalSearchScope yamlTypeScope = GlobalSearchScope.getScopeRestrictedByFileTypes(allScope, YAMLFileType.YML);
            SearchInYamlFileProcessor processor = new SearchInYamlFileProcessor(element -> {
                if(element instanceof YAMLKeyValue){
                    YAMLKeyValue keyValue = (YAMLKeyValue) element;
                    if("Name".equals(keyValue.getKeyText()) && sourceElement.getText().equals(keyValue.getValueText())) {
                        YAMLKeyValue parent1 = PsiTreeUtil.getParentOfType(keyValue, YAMLKeyValue.class);
                        return parent1 != null && "Export".equals(parent1.getKeyText());
                    }
                }
                return false;
            });
            searchHelper.processAllFilesWithWord("Export", yamlTypeScope, processor, true);
            return processor.get();
        }
        return null;
    }

}