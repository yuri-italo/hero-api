package br.com.gubee.interview.core.features.powerstats;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PowerStatsService {

    private final PowerStatsRepository powerStatsRepository;

    @Transactional
    public UUID create(PowerStats powerStats) {
        return powerStatsRepository.create(powerStats);
    }

    public PowerStats findById(UUID powerStatsId) {
        return powerStatsRepository.findById(powerStatsId);
    }

    public List<PowerStats> createPowerStatsList(List<Hero> heroes) {
        List<PowerStats> powerStatsList =  new ArrayList<>();

        heroes.forEach(hero -> {
            powerStatsList.add(powerStatsRepository.findById(hero.getPowerStatsId()));
        });

        return powerStatsList;
    }

    public void update(UUID powerStatsId, UpdateHeroRequest updateHeroRequest) {
        PowerStats powerStats = findById(powerStatsId);
        powerStatsRepository.update(powerStats,updateHeroRequest);
    }

    public void deleteById(UUID powerStatsId) {
        powerStatsRepository.delete(powerStatsId);
    }
}
