package br.com.gubee.interview.core.assembler;

import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.dto.HeroDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@NoArgsConstructor
@AllArgsConstructor
public class HeroDTOAssembler {
    @Autowired
    private PowerStatsService powerStatsService;

    public HeroDTO toDTO(Hero hero) {
        return new HeroDTO(hero,powerStatsService.findById(hero.getPowerStatsId()));
    }

    public List<HeroDTO> toCollectionDTO(List<Hero> heroes) {
        return heroes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
