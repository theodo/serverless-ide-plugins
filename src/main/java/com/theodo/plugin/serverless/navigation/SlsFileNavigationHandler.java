package com.theodo.plugin.serverless.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilCore;
import com.theodo.plugin.serverless.utils.IncludedFileHelper;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;

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
            IncludedFileHelper.PropertyInFile relativeFilePath = getRelativeFilePath(text);
            if(relativeFilePath != null){
                VirtualFile destFile = findVirtualFile(sourceElement, relativeFilePath.relativeFilePath);
                if(destFile != null) {
                    PsiFile file = PsiManager.getInstance(sourceElement.getProject()).findFile(destFile);
                    if(file instanceof YAMLFile && relativeFilePath.propertyName != null) {
                        String[] paths = relativeFilePath.propertyName.split("\\.");
                        YAMLKeyValue qualifiedKeyInFile = YAMLUtil.getQualifiedKeyInFile((YAMLFile) file, paths);
                        if(qualifiedKeyInFile != null){
                            return new PsiElement[]{qualifiedKeyInFile};
                        }
                    }
                    return new PsiElement[]{file};
                }
            }
        }

        return null;
    }
}