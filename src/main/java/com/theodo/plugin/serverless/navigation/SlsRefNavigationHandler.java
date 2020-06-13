package com.theodo.plugin.serverless.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NAVIGATE from '${self:something} to something DEFINITION
 */
public class SlsRefNavigationHandler implements GotoDeclarationHandler {
    private static final Pattern REF_PATTERN = Pattern.compile("(?:\\$\\{self:([^$^}]*))");

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        final IElementType elementType = PsiUtilCore.getElementType(sourceElement);
        if (elementType == YAMLTokenTypes.TEXT || elementType == YAMLTokenTypes.SCALAR_STRING) {
            String text = sourceElement.getText();
            Matcher matcher = REF_PATTERN.matcher(text);
            int position = offset - sourceElement.getTextOffset();
            while (matcher.find()) {
                int start = matcher.start(1);
                int end = matcher.end(1);

                if (position >= start && position < end) {
                    String group = matcher.group(1);

                    // search first in current file
                    PsiElement foundInCurrentFile = searchInFile(group, sourceElement.getContainingFile());
                    if (foundInCurrentFile != null) return new PsiElement[]{foundInCurrentFile};

                    // then, if not found, search in all other YAML files
                    Collection<VirtualFile> files = FilenameIndex.getAllFilesByExt(sourceElement.getProject(), YAMLFileType.DEFAULT_EXTENSION);
                    for (VirtualFile yamlFile : files) {
                        PsiFile file = PsiManager.getInstance(sourceElement.getProject()).findFile(yamlFile);
                        PsiElement foundInDistantFile = searchInFile(group, file);
                        if (foundInDistantFile != null) return new PsiElement[]{foundInDistantFile};
                    }
                }
            }
        }
        return null;
    }

    private PsiElement searchInFile(String group, PsiFile file) {
        String[] pathElements = group.split("\\.");
        while (pathElements.length >= 1) {
            YAMLKeyValue qualifiedKeyInFile = YAMLUtil.getQualifiedKeyInFile((YAMLFile) file, pathElements);
            if (qualifiedKeyInFile != null) return qualifiedKeyInFile;
            pathElements = Arrays.copyOf(pathElements, pathElements.length - 1);
        }
        return null;
    }

}