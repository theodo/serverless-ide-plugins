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
import com.intellij.psi.util.PsiTreeUtil;
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
import java.util.List;
import java.util.Map;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER;
import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;


public class SlsCompletionContributor extends CompletionContributor {

    private JsonSchemaObject jsonSchemaObject;
    private JsonSchemaObject jsonSchemaObjectNo;

    public SlsCompletionContributor() {
        extend(CompletionType.BASIC,
                getCompletionPattern(),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        PsiElement position = parameters.getPosition();
                        JsonSchemaObject jsonSchemaObject = lazyLoadSchema(position);
                        JsonSchemaObject jsonSchemaObjectWithoutCF = lazyLoadSchemaWithoutCF(position);

                        if (jsonSchemaObject != null
                                && jsonSchemaObjectWithoutCF != null
                                && jsonSchemaObject.getDefinitionsMap() != null
                                && jsonSchemaObjectWithoutCF.getDefinitionsMap() != null) {

                            String parentType = getParentType(position);
                            if(DUMMY_IDENTIFIER_TRIMMED.equals(parentType)) parentType = null;

                            if(parentType != null){
                                jsonSchemaObject = jsonSchemaObjectWithoutCF;

                                JsonSchemaObject relativeDefinition = jsonSchemaObject.findRelativeDefinition("#/definitions/resources");
                                String matchingRef = find(parentType, jsonSchemaObject.getDefinitionsMap());
                                if(matchingRef != null && relativeDefinition != null){
                                    JsonSchemaObject specificJsonSchema = jsonSchemaObject.findRelativeDefinition("#/definitions/" + matchingRef);
                                    JsonSchemaObject zzz = relativeDefinition.getMatchingPatternPropertySchema("zzz");
                                    if(zzz != null && specificJsonSchema != null) {
                                        zzz.getProperties().clear();
                                        zzz.getProperties().putAll(specificJsonSchema.getProperties());
                                    }
                                }
                            }
                            JsonSchemaCompletionContributor.doCompletion(parameters, resultSet, jsonSchemaObject, false);
                        }
                    }
                }
        );
    }

    private String find(String value, Map<String, JsonSchemaObject> definitionsMap) {
        value = value.replace("\'", "\"").replace("\"", "");
        value = "\""+value+"\"";

        for (Map.Entry<String, JsonSchemaObject> entry : definitionsMap.entrySet()) {
            JsonSchemaObject jsonSchemaObject = entry.getValue();
            Map<String, JsonSchemaObject> properties = jsonSchemaObject.getProperties();
            JsonSchemaObject type = properties.get("Type");
            if(type == null) continue;
            List<Object> list = type.getEnum();
            if(list != null && list.contains(value)) return entry.getKey();
        }
        return null;
    }

    private String getParentType(PsiElement position){
        if(position instanceof YAMLKeyValue){
            YAMLKeyValue yamlKeyValue =(YAMLKeyValue) position;
            if(yamlKeyValue.getKey() != null &&
                    yamlKeyValue.getValue() != null &&
                    "Type".equals(yamlKeyValue.getKey().getText())) return yamlKeyValue.getValue().getText();
        }

        YAMLKeyValue prevSiblingOfType = PsiTreeUtil.getPrevSiblingOfType(position, YAMLKeyValue.class);
        while(prevSiblingOfType != null){
            if(prevSiblingOfType.getKey() != null &&
                    prevSiblingOfType.getValue() != null &&
                    "Type".equals(prevSiblingOfType.getKey().getText())) return prevSiblingOfType.getValue().getText();
            prevSiblingOfType = PsiTreeUtil.getPrevSiblingOfType(prevSiblingOfType, YAMLKeyValue.class);
        }

        YAMLKeyValue parentOfType = PsiTreeUtil.getParentOfType(position, YAMLKeyValue.class);
        if(parentOfType != null) return getParentType(parentOfType);
        return null;
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
    private @org.jetbrains.annotations.Nullable JsonSchemaObject lazyLoadSchemaWithoutCF(PsiElement position) {
        try {
            if(jsonSchemaObjectNo == null) {
                InputStream stream = SlsCompletionContributor.class.getClassLoader().getResourceAsStream("/serverless_no.json");
                if(stream == null) return null;

                String schemaAsString = FileUtil.loadTextAndClose(new InputStreamReader(stream));
                VirtualFile virtualFile = new LightVirtualFile("schemaNo.json", schemaAsString);
                JsonSchemaService jsonSchemaService = JsonSchemaService.Impl.get(position.getProject());
                jsonSchemaObjectNo = jsonSchemaService.getSchemaObjectForSchemaFile(virtualFile);
            }
        } catch (Exception ignored) {
        }
        return jsonSchemaObjectNo;
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