package br.com.gubee.interview.model.dto;

import br.com.gubee.interview.model.PowerStats;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PowerStatsDTO {
    private int strength;
    private int agility;
    private int dexterity;
    private int intelligence;

    public PowerStatsDTO(PowerStats powerStats) {
        this.strength = powerStats.getStrength();
        this.agility = powerStats.getAgility();
        this.dexterity = powerStats.getDexterity();
        this.intelligence = powerStats.getIntelligence();
    }
}
