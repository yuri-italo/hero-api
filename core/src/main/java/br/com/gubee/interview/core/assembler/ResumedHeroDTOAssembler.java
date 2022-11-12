package br.com.gubee.interview.core.assembler;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.dto.ResumedHeroDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResumedHeroDTOAssembler {
    public ResumedHeroDTO toDTO(Hero hero) {
        return new ResumedHeroDTO(hero);
    }

    public List<ResumedHeroDTO> toCollectionDTO(List<Hero> heroes) {
        return heroes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
