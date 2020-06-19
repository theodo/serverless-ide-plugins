package com.theodo.plugin.serverless.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchInYamlFileProcessor implements Processor<PsiFile> {

    private final PsiElementFilter filter;
    private final List<PsiElement> foundElements = new ArrayList<>();

    public SearchInYamlFileProcessor(PsiElementFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean process(PsiFile psiFile) {
        if (psiFile instanceof YAMLFile){
            YAMLFile yamlFile = (YAMLFile) psiFile;
            PsiElement[] keys = PsiTreeUtil.collectElements(yamlFile, filter);
            foundElements.addAll(Arrays.asList(keys));
        }
        return true;
    }

    public PsiElement[] get() {
        if(foundElements.isEmpty()) return null;
        return foundElements.toArray(new PsiElement[0]);
    }
}
