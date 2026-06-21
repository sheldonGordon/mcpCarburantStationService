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
        String json = p.getValueAsString();
        if (json == null || json.isEmpty()) return null;

        // On utilise le codec déjà configuré dans le contexte de Jackson
        // On crée un parser pour la chaîne JSON interne
        return p.getCodec().readValue(p.getCodec().getFactory().createParser(json), type);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        // On récupère le type de l'objet (FermetureDTO, RuptureDTO, etc.)
        this.type = ctxt.getContextualType();
        return this;
    }
}
