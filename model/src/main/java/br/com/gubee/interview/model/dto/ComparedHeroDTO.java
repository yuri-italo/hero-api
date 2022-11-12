package br.com.gubee.interview.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ComparedHeroDTO {
    private UUID id;
    private String name;
    private int strength;
    private int agility;
    private int dexterity;
    private int intelligence;

    public ComparedHeroDTO(HeroDTO hero, HeroDTO heroToCompare) {
        this.id = hero.getId();
        this.name = hero.getName();
        this.strength = hero.getPowerStats().getStrength() - heroToCompare.getPowerStats().getStrength();
        this.agility = hero.getPowerStats().getAgility() - heroToCompare.getPowerStats().getAgility();
        this.dexterity = hero.getPowerStats().getDexterity() - heroToCompare.getPowerStats().getDexterity();
        this.intelligence = hero.getPowerStats().getIntelligence() - heroToCompare.getPowerStats().getIntelligence();
    }
}
