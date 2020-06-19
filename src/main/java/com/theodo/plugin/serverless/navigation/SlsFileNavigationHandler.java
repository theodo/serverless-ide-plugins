package com.theodo.plugin.serverless.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;

import static com.theodo.plugin.serverless.utils.IncludedFileHelper.findVirtualFile;
import static com.theodo.plugin.serverless.utils.IncludedFileHelper.getRelativeFilePath;

/**
 * NAVIGATE from '${file:path/path/file} TO the file ./path/path/file
 */
public class SlsFileNavigationHandler implements GotoDeclarationHandler {

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        final IElementType elementType = PsiUtilCore.getElementType(sourceElement);
        if (elementType == YAMLTokenTypes.TEXT || elementType == YAMLTokenTypes.SCALAR_STRING) {
            String text = sourceElement.getText();
            String relativeFilePath = getRelativeFilePath(text);
            if(relativeFilePath != null){
                VirtualFile destFile = findVirtualFile(sourceElement, relativeFilePath);
                if(destFile != null) {
                    PsiFile file = PsiManager.getInstance(sourceElement.getProject()).findFile(destFile);
                    return new PsiElement[]{file};
                }
            }
        }

        return null;
    }
}