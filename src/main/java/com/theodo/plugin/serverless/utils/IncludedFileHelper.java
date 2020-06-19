package com.theodo.plugin.serverless.utils;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncludedFileHelper {
    private static final Pattern FILE_PATTERN = Pattern.compile("\\$\\{file\\((.*)\\).*}");

    public static String getRelativeFilePath(String textElement){
        Matcher matcher = FILE_PATTERN.matcher(textElement);
        if(matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public static @Nullable VirtualFile findVirtualFile(PsiElement sourceElement, String relativeFilePath){
        PsiFile containingFile = sourceElement.getContainingFile();
        VirtualFile virtualFile = containingFile.getVirtualFile();
        VirtualFile parent = virtualFile.getParent();
        return  LocalFileSystem.getInstance().findFileByPath(parent.getPath() + File.separator + relativeFilePath);
    }
}
