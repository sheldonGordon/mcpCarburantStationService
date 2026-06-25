package fr.chatelain.mcp.carburantstationservice.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;

public class StringToJsonDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

    private JavaType type;

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        // On récupère le nœud sous forme d'arbre (JsonNode)
        JsonNode node = p.readValueAsTree();

        // Si c'est un objet, on le convertit directement
        if (node.isObject()) {
            return p.getCodec().treeToValue(node, type.getRawClass());
        }

        // Si c'est une chaîne, on la re-parse (cas où le JSON est échappé)
        if (node.isTextual()) {
            String json = node.asText();
            if (json == null || json.isEmpty()) return null;
            try (JsonParser inner = p.getCodec().getFactory().createParser(json)) {
                return p.getCodec().readValue(inner, type);
            }
        }
        return null;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        // On récupère le type de l'objet (FermetureDTO, RuptureDTO, etc.)
        this.type = ctxt.getContextualType();
        return this;
    }
}
