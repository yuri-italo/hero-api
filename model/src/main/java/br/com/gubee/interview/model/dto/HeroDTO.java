package br.com.gubee.interview.model.dto;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.enums.Race;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Getter
@NoArgsConstructor
public class HeroDTO {
    private UUID id;
    private String name;
    private Race race;
    private String createdAt;
    private String updatedAt;
    private boolean enabled;
    private PowerStatsDTO powerStats;

    public HeroDTO(Hero hero, PowerStats powerStats) {
        this.id = hero.getId();
        this.name = hero.getName();
        this.race = hero.getRace();
        this.createdAt = String.valueOf(hero.getCreatedAt());
        this.updatedAt = String.valueOf(hero.getUpdatedAt());
        this.enabled = hero.isEnabled();
        this.powerStats = new PowerStatsDTO(powerStats);
    }
}
