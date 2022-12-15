package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.dto.UpdatedHeroDTO;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HeroService {

    private final HeroRepository heroRepository;
    private final PowerStatsService powerStatsService;

    @Transactional
    public UUID create(CreateHeroRequest createHeroRequest) {
        UUID uuid = powerStatsService.create(new PowerStats(createHeroRequest));
        return heroRepository.create(new Hero(createHeroRequest,uuid));
    }

    public Optional<Hero> findById(UUID uuid) {
        return heroRepository.findById(uuid);
    }

    public List<Hero> findManyByName(String heroName) {
        return heroRepository.findManyByName(heroName);
    }

    public Optional<Hero> findByName(String heroName) {
        return heroRepository.findByName(heroName);
    }
    public List<Hero> findAll() {
        return heroRepository.findAll();
    }

    public Hero update(Hero hero, UpdatedHeroDTO updateHeroDTO) {
        heroRepository.update(hero,updateHeroDTO);
        powerStatsService.update(hero.getPowerStatsId(),updateHeroDTO);
        return hero;
    }

    public void delete(Hero hero) {
        UUID powerStatsId = hero.getPowerStatsId();
        heroRepository.delete(hero);
        powerStatsService.deleteById(powerStatsId);
    }
}
