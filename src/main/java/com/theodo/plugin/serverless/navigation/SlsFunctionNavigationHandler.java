package com.theodo.plugin.serverless.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.io.File;

/**
 * NAVIGATE from 'handler: functions/directory/directory/file:function TO code of the function
 */
public class SlsFunctionNavigationHandler implements GotoDeclarationHandler {

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        final IElementType elementType = PsiUtilCore.getElementType(sourceElement);
        if (elementType == YAMLTokenTypes.TEXT || elementType == YAMLTokenTypes.SCALAR_STRING) {
            YAMLKeyValue parent = PsiTreeUtil.getParentOfType(sourceElement, YAMLKeyValue.class);
            if (parent == null) return null;

            if (!"handler".equals(parent.getKeyText())) {
                return null;
            }

            if (!isLambda(parent)) return null;

            String text = sourceElement.getText();
            String[] fileAndMethod = text.split("\\.");
            if (fileAndMethod.length >= 1) {
                PsiFile containingFile = sourceElement.getContainingFile();
                VirtualFile virtualFile = containingFile.getVirtualFile();
                VirtualFile directory = virtualFile.getParent();

                PsiElement codeFile = findMatchingPsiFile(sourceElement, fileAndMethod[0] + ".ts", directory);
                if (codeFile != null) return new PsiElement[] {codeFile};

                codeFile = findMatchingPsiFile(sourceElement, fileAndMethod[0] + ".js", directory);
                if (codeFile != null) return new PsiElement[] {codeFile};

                codeFile = findMatchingPsiFile(sourceElement, fileAndMethod[0] + ".py", directory);
                if (codeFile != null) return new PsiElement[] {codeFile};

            }

            return null;
        }
        return null;
    }

    public static PsiElement findMatchingPsiFile(@NotNull PsiElement sourceElement, String relativePath, VirtualFile directory) {
        VirtualFile destFile = LocalFileSystem.getInstance().findFileByPath(directory.getPath() + File.separator + relativePath);
        if (destFile != null) {
            return PsiManager.getInstance(sourceElement.getProject()).findFile(destFile);
        }
        return null;
    }

    public static boolean isLambda(YAMLKeyValue parent) {
        while (parent != null) {
            if (parent.getKeyText().equals("functions")) {
                return true;
            }
            parent = PsiTreeUtil.getParentOfType(parent, YAMLKeyValue.class);
        }
        return false;
    }

}