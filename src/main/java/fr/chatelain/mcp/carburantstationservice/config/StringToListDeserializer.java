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
        String json = p.getValueAsString();
        if (json == null || json.isEmpty() || "null".equals(json)) {
            return Collections.emptyList();
        }

        JsonParser innerParser = p.getCodec().getFactory().createParser(json);

        // On vérifie si la chaîne interne est un tableau ou un objet
        if (innerParser.nextToken() == JsonToken.START_ARRAY) {
            // Lecture d'une liste : [{}, {}]
            return p.getCodec().readValue(innerParser,
                    ctxt.getTypeFactory().constructCollectionType(List.class, elementType));
        } else {
            // Lecture d'un objet unique : {} -> on le transforme en liste
            Object singleObject = p.getCodec().readValue(innerParser, elementType);
            return Collections.singletonList(singleObject);
        }
    }
}