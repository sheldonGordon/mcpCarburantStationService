package fr.chatelain.mcp.carburantstationservice.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;

import java.time.LocalDateTime;

@Document(collection = "ruptures_carburants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RuptureCarburant {
    @Id
    private String id;
    
    @Field("carburant")
    private CarburantType carburant;
    
    @Field("debut")
    private LocalDateTime debut;
    
    @Field("fin")
    private LocalDateTime fin;

    @Field("type")
    private String type;
}

