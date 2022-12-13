package br.com.gubee.interview.model.dto;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.enums.Race;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    public static List<HeroDTO> toCollectionDTO(List<Hero> heroes, List<PowerStats> powerStats) {
        HeroDTO heroDTO;
        List<HeroDTO> heroDTOList = new ArrayList<>();

        for (int i = 0; i < heroes.size(); i++) {
            heroDTO = new HeroDTO(heroes.get(i),powerStats.get(i));
            heroDTOList.add(heroDTO);
        }

        return heroDTOList;
    }
}
