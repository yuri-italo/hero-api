package br.com.gubee.interview.core.features.powerstats;

import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.dto.UpdatedHeroDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void update(UUID powerStatsId, UpdatedHeroDTO updateHeroDTO) {
        PowerStats powerStats = findById(powerStatsId);
        powerStatsRepository.update(powerStats,updateHeroDTO);
    }

    public void deleteById(UUID powerStatsId) {
        powerStatsRepository.delete(powerStatsId);
    }
}
