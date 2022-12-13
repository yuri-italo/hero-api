package br.com.gubee.interview.core.assembler;

import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.dto.ResumedHeroDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ResumedHeroDTOAssembler {
    private final PowerStatsService powerStatsService;

    public ResumedHeroDTOAssembler(PowerStatsService powerStatsService) {
        this.powerStatsService = powerStatsService;
    }

    public ResumedHeroDTO toDTO(Hero hero) {
        return new ResumedHeroDTO(hero,powerStatsService.findById(hero.getPowerStatsId()));
    }

    public List<ResumedHeroDTO> toCollectionDTO(List<Hero> heroes) {
        return heroes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
