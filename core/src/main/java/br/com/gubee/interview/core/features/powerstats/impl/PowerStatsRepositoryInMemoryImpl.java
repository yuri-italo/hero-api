package br.com.gubee.interview.core.features.powerstats.impl;

import br.com.gubee.interview.core.features.powerstats.PowerStatsRepository;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@RequiredArgsConstructor
@NoRepositoryBean
@Getter
public class PowerStatsRepositoryInMemoryImpl implements PowerStatsRepository {
    public static final Map<UUID, PowerStats> powerStatsStorage = new TreeMap<>();

    @Override
    public UUID create(PowerStats powerStats) {
        if (powerStats.getId() != null)
            throw new IllegalArgumentException();

        UUID uuid = UUID.randomUUID();
        Instant now = Instant.now();

        powerStats.setId(uuid);
        powerStats.setCreatedAt(now);
        powerStats.setUpdatedAt(now);

        powerStatsStorage.put(uuid,powerStats);

        return uuid;
    }

    @Override
    public PowerStats findById(UUID powerStatsId) {
        return powerStatsStorage.get(powerStatsId);
    }

    @Override
    public void update(PowerStats powerStats, UpdateHeroRequest updateHeroRequest) {
        if (powerStats == null || updateHeroRequest == null)
            throw new NullPointerException();

        PowerStats modifiedPowerStats = changeFields(powerStats, updateHeroRequest);
        powerStatsStorage.put(powerStats.getId(),modifiedPowerStats);
    }

    @Override
    public void delete(UUID powerStatsId) {
        if (powerStatsStorage.get(powerStatsId) == null)
            throw new NullPointerException();

        powerStatsStorage.remove(powerStatsId);
    }

    private PowerStats changeFields(PowerStats powerStats, UpdateHeroRequest updateHeroRequest) {
        if (updateHeroRequest.getStrength() != null && !(powerStats.getStrength() == updateHeroRequest.getStrength()))
            powerStats.setStrength(updateHeroRequest.getStrength());

        if (updateHeroRequest.getAgility() != null && !(powerStats.getAgility() == updateHeroRequest.getAgility()))
            powerStats.setAgility(updateHeroRequest.getAgility());

        if (updateHeroRequest.getDexterity() != null && !(powerStats.getDexterity() == updateHeroRequest.getDexterity()))
            powerStats.setDexterity(updateHeroRequest.getDexterity());

        if (updateHeroRequest.getStrength() != null && !(powerStats.getIntelligence() == updateHeroRequest.getIntelligence()))
            powerStats.setIntelligence(updateHeroRequest.getIntelligence());

        powerStats.setUpdatedAt(Instant.now());

        return powerStats;
    }
}
