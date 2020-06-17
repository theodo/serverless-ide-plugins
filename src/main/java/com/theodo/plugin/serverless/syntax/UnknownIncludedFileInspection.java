package com.theodo.plugin.serverless.syntax;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLSequenceItem;
import org.jetbrains.yaml.psi.YAMLValue;
import org.jetbrains.yaml.psi.YamlPsiElementVisitor;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnknownIncludedFileInspection extends LocalInspectionTool {

    private static final Pattern FILE_PATTERN = Pattern.compile("\\$\\{file\\((.*)\\)\\}");

    public @Nullable String getStaticDescription() {
        return "Highlight Undefined Included files";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new YamlPsiElementVisitor() {

            @Override
            public void visitKeyValue(@NotNull YAMLKeyValue keyValue) {
                if(keyValue.getValue() != null) {
                    detect(keyValue.getValue());
                }
            }

            @Override
            public void visitSequenceItem(@NotNull YAMLSequenceItem sequenceItem) {
                if(sequenceItem.getValue() != null) {
                    detect(sequenceItem.getValue());
                }
            }

            private void detect(YAMLValue sourceElement) {
                String text = sourceElement.getText();
                Matcher matcher = FILE_PATTERN.matcher(text);
                if(matcher.matches()) {
                    String destination = matcher.group(1);
                    PsiFile containingFile = sourceElement.getContainingFile();
                    VirtualFile virtualFile = containingFile.getVirtualFile();
                    VirtualFile parent = virtualFile.getParent();

                    VirtualFile destFile = LocalFileSystem.getInstance().findFileByPath(parent.getPath() + File.separator + destination);
                    if(destFile == null){
                        holder.registerProblem(sourceElement, "Included File not found");
                    }
                }
            }
        };
    }
}
