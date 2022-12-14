package br.com.gubee.interview.model.dto;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.enums.Race;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ResumedHeroDTO {
    private UUID id;
    private String name;
    private Race race;
    private int strength;
    private int agility;
    private int dexterity;
    private int intelligence;
    public ResumedHeroDTO(Hero hero, PowerStats powerStats) {
        this.id = hero.getId();
        this.name = hero.getName();
        this.race = hero.getRace();
        this.strength = powerStats.getStrength();
        this.agility = powerStats.getAgility();
        this.dexterity = powerStats.getDexterity();
        this.intelligence = powerStats.getIntelligence();
    }

    public static List<ResumedHeroDTO> toCollectionDTO(List<Hero> heroes, List<PowerStats> powerStats) {
        List<ResumedHeroDTO> resumedHeroDTOList = new ArrayList<>();
        ResumedHeroDTO resumedHeroDTO;

        for (int i = 0; i < heroes.size(); i++) {
            resumedHeroDTO = new ResumedHeroDTO(heroes.get(i),powerStats.get(i));
            resumedHeroDTOList.add(resumedHeroDTO);
        }

        return resumedHeroDTOList;
    }
}
