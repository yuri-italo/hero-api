package br.com.gubee.interview.model.dto;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.enums.Race;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ResumedHeroDTO {
    private UUID id;
    private String name;
    private Race race;

    public ResumedHeroDTO(Hero hero) {
        this.id = hero.getId();
        this.name = hero.getName();
        this.race = hero.getRace();
    }
}
