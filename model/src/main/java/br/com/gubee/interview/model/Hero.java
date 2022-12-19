package br.com.gubee.interview.model;

import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode
public class Hero {
    @EqualsAndHashCode.Exclude
    private UUID id;

    @EqualsAndHashCode.Include
    private String name;

    @EqualsAndHashCode.Exclude
    private Race race;

    @EqualsAndHashCode.Exclude
    private UUID powerStatsId;

    @EqualsAndHashCode.Exclude
    private Instant createdAt;

    @EqualsAndHashCode.Exclude
    private Instant updatedAt;

    @EqualsAndHashCode.Exclude
    private boolean enabled;

    public Hero(CreateHeroRequest createHeroRequest, UUID powerStatsId) {
        this.name = createHeroRequest.getName();
        this.race = createHeroRequest.getRace();
        this.powerStatsId = powerStatsId;
    }
}
