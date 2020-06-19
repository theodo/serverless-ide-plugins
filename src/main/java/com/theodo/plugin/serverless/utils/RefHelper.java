package com.theodo.plugin.serverless.utils;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.util.PsiElementFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YamlRecursivePsiElementVisitor;

import java.util.ArrayList;
import java.util.List;

import static com.theodo.plugin.serverless.navigation.SlsFunctionNavigationHandler.isLambda;

public class RefHelper {
    public static PsiElement[] searchReference(PsiElement sourceElement) {
        if(sourceElement == null) return null;

        String searchedText = getLambdaRefName(sourceElement);
        boolean restrictToLambda = hasLambdaSuffix(sourceElement);

        PsiElementFilter psiElementFilter = element -> {
            if(!(element instanceof YAMLKeyValue)) return false;
            YAMLKeyValue keyValue = (YAMLKeyValue) element;
            if(restrictToLambda && !isLambda(keyValue)) return false;

            if(keyValue.getKeyText().equals(searchedText)) return true;
            if(StringUtil.capitalize(keyValue.getKeyText()).equals(searchedText)) return true;

            return "id".equals(keyValue.getKeyText()) && keyValue.getValueText().equals(searchedText);
        };

        // search in current file first
        PsiElement[] foundDefinitionsInCurrentFile = search(psiElementFilter, sourceElement.getContainingFile());
        if(foundDefinitionsInCurrentFile.length > 0){
            return foundDefinitionsInCurrentFile;
        }

        // search in other files if not found
        PsiSearchHelper searchHelper = PsiSearchHelper.getInstance(sourceElement.getProject());
        GlobalSearchScope allScope = GlobalSearchScope.allScope(sourceElement.getProject());
        GlobalSearchScope yamlTypeScope = GlobalSearchScope.getScopeRestrictedByFileTypes(allScope, YAMLFileType.YML);
        SearchInYamlFileProcessor processor = new SearchInYamlFileProcessor(psiElementFilter);
        searchHelper.processAllFilesWithWord(searchedText, yamlTypeScope, processor, true);
        return processor.get();
    }

    private static boolean hasLambdaSuffix(PsiElement sourceElement) {
        return sourceElement.getText().endsWith("LambdaFunction");
    }

    private static PsiElement[] search(PsiElementFilter psiElementFilter, PsiFile containingFile) {
        List<PsiElement> foundMatchingElements = new ArrayList<>();
        containingFile.acceptChildren(new SearchInYamlFile(psiElementFilter, foundMatchingElements));
        return foundMatchingElements.toArray(new PsiElement[0]);
    }

    @NotNull
    private static String getLambdaRefName(PsiElement sourceElement) {
        String searchedText = sourceElement.getText();
        if(searchedText.endsWith("LambdaFunction")){
            int index = searchedText.lastIndexOf("LambdaFunction");
            searchedText = searchedText.substring(0, index);
        }
        return searchedText;
    }

    private static class SearchInYamlFile extends YamlRecursivePsiElementVisitor {
        private final PsiElementFilter psiElementFilter;
        private final List<PsiElement> foundMatchingElements;

        public SearchInYamlFile(PsiElementFilter psiElementFilter, List<PsiElement> foundMatchingElements) {
            this.psiElementFilter = psiElementFilter;
            this.foundMatchingElements = foundMatchingElements;
        }

        @Override
        public void visitKeyValue(@NotNull YAMLKeyValue keyValue) {
            if(psiElementFilter.isAccepted(keyValue)){
                foundMatchingElements.add(keyValue);
            }
            super.visitKeyValue(keyValue);
        }
    }
}
