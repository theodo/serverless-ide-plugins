package com.theodo.plugin.serverless.navigation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilCore;
import com.theodo.plugin.serverless.completion.SlsCompletionContributor;
import org.apache.xerces.parsers.SAXParser;
import org.globsframework.saxstack.parser.DefaultXmlNode;
import org.globsframework.saxstack.parser.SaxStackParser;
import org.globsframework.saxstack.parser.SilentXmlNode;
import org.globsframework.saxstack.parser.XmlNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.xml.sax.Attributes;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SHOWS Documentation from Amazon from Aws::xxx::yyyy resources
 */
public class SlsResourceDocumentationDefinition extends AbstractDocumentationProvider {

    private static final String AWS_RESOURCE = "aws::";
    private static final Map<String, String> URL_BY_TYPES = new HashMap<>();

    @Override
    public @Nullable PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        final IElementType elementType = PsiUtilCore.getElementType(contextElement);
        if (elementType == YAMLTokenTypes.TEXT || elementType == YAMLTokenTypes.SCALAR_STRING) {
            if (contextElement.getText().replaceAll("'", "").toLowerCase().startsWith(AWS_RESOURCE)) {
                return contextElement;
            }
        }
        return super.getCustomDocumentationElement(editor, file, contextElement);
    }

    @Override
    public @Nullable List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        final IElementType elementType = PsiUtilCore.getElementType(originalElement);
        if (elementType == YAMLTokenTypes.TEXT || elementType == YAMLTokenTypes.SCALAR_STRING) {
            if (originalElement.getText().replaceAll("'", "").toLowerCase().startsWith(AWS_RESOURCE)) {
                String url = getUrl(originalElement.getText().replaceAll("'", ""));
                if (url != null) {
                    return List.of(url);
                }
            }
        }
        return super.getUrlFor(element, originalElement);
    }

    private String getUrl(String item) {
        if (URL_BY_TYPES.isEmpty()) {
            InputStream stream = SlsCompletionContributor.class.getClassLoader().getResourceAsStream("/cloudformation-metadata.xml");
            if (stream == null) {
                URL_BY_TYPES.put("ERROR", "ERROR");
                return null;
            }
            SaxStackParser.parse(new SAXParser(), new DefaultXmlNode() {
                public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) {
                    if ("ResourceType".equals(childName)) {
                        return new ResourceNode();
                    }
                    return this;
                }
            }, new InputStreamReader(stream));
        }
        return URL_BY_TYPES.get(item);
    }

    private static class ResourceNode extends DefaultXmlNode {
        private String name;
        private String url;

        @Override
        public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) {
            if ("name".equals(childName)) {
                return new DefaultXmlNode() {
                    @Override
                    public void setValue(String value) {
                        name = value;
                    }
                };
            }
            if ("url".equals(childName)) {
                return new DefaultXmlNode() {
                    @Override
                    public void setValue(String value) {
                        url = value;
                    }
                };
            }
            return SilentXmlNode.INSTANCE;
        }

        @Override
        public void complete() {
            URL_BY_TYPES.put(name, url);
        }
    }
}