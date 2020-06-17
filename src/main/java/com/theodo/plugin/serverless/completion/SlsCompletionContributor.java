package com.theodo.plugin.serverless.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiFilePattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.ProcessingContext;
import com.jetbrains.jsonSchema.ide.JsonSchemaService;
import com.jetbrains.jsonSchema.impl.JsonSchemaCompletionContributor;
import com.jetbrains.jsonSchema.impl.JsonSchemaObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;

import java.io.InputStream;
import java.io.InputStreamReader;


public class SlsCompletionContributor extends CompletionContributor {

    private JsonSchemaObject jsonSchemaObject;

    public SlsCompletionContributor() {
        extend(CompletionType.BASIC,
                getCompletionPattern(),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        PsiElement position = parameters.getPosition();
                        JsonSchemaObject jsonSchemaObject = lazyLoadSchema(position);
                        if (jsonSchemaObject != null) {
                          JsonSchemaCompletionContributor.doCompletion(parameters, resultSet, jsonSchemaObject, false);
                        }
                    }
                }
        );
    }

    private @org.jetbrains.annotations.Nullable JsonSchemaObject lazyLoadSchema(PsiElement position) {
        try {
            if(jsonSchemaObject == null) {
                InputStream stream = SlsCompletionContributor.class.getClassLoader().getResourceAsStream("/serverless.json");
                if(stream == null) return null;

                String schemaAsString = FileUtil.loadTextAndClose(new InputStreamReader(stream));
                VirtualFile virtualFile = new LightVirtualFile("schema.json", schemaAsString);
                JsonSchemaService jsonSchemaService = JsonSchemaService.Impl.get(position.getProject());
                jsonSchemaObject = jsonSchemaService.getSchemaObjectForSchemaFile(virtualFile);
            }
        } catch (Exception ignored) {
        }
        return jsonSchemaObject;
    }

    private static ElementPattern<PsiElement> getCompletionPattern() {
        return PlatformPatterns.psiElement().withParent(PlatformPatterns.or(
                PlatformPatterns.psiElement(YAMLDocument.class),
                PlatformPatterns.psiElement(YAMLScalar.class),
                PlatformPatterns.psiElement(YAMLKeyValue.class)
        )).inFile(getSlsPattern());
    }

    private static PsiFilePattern.Capture<PsiFile> getSlsPattern() {
        return PlatformPatterns.psiFile().with(SLS_YAML_CONFIGURATION);
    }

    private static final PatternCondition<PsiFile> SLS_YAML_CONFIGURATION = new PatternCondition<PsiFile>("SLS definition files") {
        @Override
        public boolean accepts(@NotNull PsiFile psiFile, ProcessingContext processingContext) {
            return psiFile.getFileType() == YAMLFileType.YML;// Without any other indicator in the file itself, it's difficult to say if a YAML is or not 'serverless'
        }
    };
}