package br.com.gubee.interview.model.dto;

import br.com.gubee.interview.model.enums.Race;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedHeroDTO {
    private String id;
    private String name;
    private Race race;
    private Integer strength;
    private Integer agility;
    private Integer dexterity;
    private Integer intelligence;
    private Boolean enabled;
}
