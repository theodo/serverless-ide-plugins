package com.theodo.plugin.serverless.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NAVIGATE from '${file:path/path/file} TO the file ./path/path/file
 */
public class SlsFileNavigationHandler implements GotoDeclarationHandler {
    private static final Pattern FILE_PATTERN = Pattern.compile("\\$\\{file\\((.*)\\).*}");

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        final IElementType elementType = PsiUtilCore.getElementType(sourceElement);
        if (elementType == YAMLTokenTypes.TEXT || elementType == YAMLTokenTypes.SCALAR_STRING) {
            String text = sourceElement.getText();
            Matcher matcher = FILE_PATTERN.matcher(text);
            if(matcher.matches()){
                String destination = matcher.group(1);
                PsiFile containingFile = sourceElement.getContainingFile();
                VirtualFile virtualFile = containingFile.getVirtualFile();
                VirtualFile parent = virtualFile.getParent();

                VirtualFile destFile = LocalFileSystem.getInstance().findFileByPath(parent.getPath() + File.separator + destination);
                if(destFile != null) {
                    PsiFile file = PsiManager.getInstance(sourceElement.getProject()).findFile(destFile);
                    return new PsiElement[]{file};
                }
            }
        }

        return null;
    }
}