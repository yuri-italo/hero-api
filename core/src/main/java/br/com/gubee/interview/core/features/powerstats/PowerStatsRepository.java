package br.com.gubee.interview.core.features.powerstats;

import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface PowerStatsRepository {
    UUID create(PowerStats powerStats);
    PowerStats findById(UUID powerStatsId);
    void update(PowerStats powerStats, UpdateHeroRequest updateHeroRequest);
    void delete(UUID powerStatsId);
}
