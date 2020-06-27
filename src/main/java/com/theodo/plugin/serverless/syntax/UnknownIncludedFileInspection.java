package com.theodo.plugin.serverless.syntax;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.theodo.plugin.serverless.utils.IncludedFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.*;

import static com.theodo.plugin.serverless.utils.IncludedFileHelper.findVirtualFile;
import static com.theodo.plugin.serverless.utils.IncludedFileHelper.getRelativeFilePath;

public class UnknownIncludedFileInspection extends LocalInspectionTool {


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
                IncludedFileHelper.PropertyInFile relativeFilePath = getRelativeFilePath(text);
                if(relativeFilePath != null) {
                    VirtualFile destFile = findVirtualFile(sourceElement, relativeFilePath.relativeFilePath);
                    if(destFile == null){
                        holder.registerProblem(sourceElement, "Included File not found");
                        return;
                    }

                    PsiFile file = PsiManager.getInstance(sourceElement.getProject()).findFile(destFile);
                    if(file instanceof YAMLFile && !relativeFilePath.propertyName.isBlank()) {
                        String[] paths = relativeFilePath.propertyName.split("\\.");
                        YAMLKeyValue qualifiedKeyInFile = YAMLUtil.getQualifiedKeyInFile((YAMLFile) file, paths);
                        if(qualifiedKeyInFile == null){
                            holder.registerProblem(sourceElement, "Property not found in included file");
                        }
                    }
                }
            }
        };
    }
}
