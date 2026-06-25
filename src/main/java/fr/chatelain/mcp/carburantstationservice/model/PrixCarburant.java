package fr.chatelain.mcp.carburantstationservice.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Document(collection = "prix_carburants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PrixCarburant {
    @Id
    private String id;
    
    @Field("carburant")
    private CarburantType carburant;
    
    @Field("maj")
    private LocalDateTime maj;
    
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal valeur;

    @Field("id_station")
    private Long idStation;
}

