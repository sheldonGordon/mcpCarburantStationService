package fr.chatelain.mcp.carburantstationservice.config;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class StringToListDeserializer extends JsonDeserializer<List<?>> implements ContextualDeserializer {

    private JavaType elementType;

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        // On récupère le type générique de la List (ex: FermetureDTO ou RuptureDTO)
        this.elementType = ctxt.getContextualType().getContentType();
        return this;
    }

    @Override
    public List<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();

        // 1. Si c'est déjà une liste : conversion directe
        if (node.isArray()) {
            JavaType listType = ctxt.getTypeFactory().constructCollectionType(List.class, elementType);
            return ctxt.readTreeAsValue(node, listType);
        }

        // 2. Si c'est un OBJET UNIQUE : on le transforme en liste de 1 élément
        if (node.isObject()) {
            Object singleObject = ctxt.readTreeAsValue(node, elementType);
            return Collections.singletonList(singleObject);
        }

        // 3. Si c'est une chaîne JSON échappée : on re-parse
        if (node.isTextual()) {
            String json = node.asText();
            if (json == null || json.isEmpty() || "null".equals(json)) return Collections.emptyList();

            try (JsonParser inner = p.getCodec().getFactory().createParser(json)) {
                JsonNode innerNode = inner.readValueAsTree();
                if (innerNode.isArray()) {
                    return ctxt.readTreeAsValue(innerNode, ctxt.getTypeFactory().constructCollectionType(List.class, elementType));
                } else {
                    return Collections.singletonList(ctxt.readTreeAsValue(innerNode, elementType));
                }
            }
        }

        return Collections.emptyList();
    }
}