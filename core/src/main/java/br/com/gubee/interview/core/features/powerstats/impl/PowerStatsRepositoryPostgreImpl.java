package br.com.gubee.interview.core.features.powerstats.impl;

import br.com.gubee.interview.core.features.powerstats.PowerStatsRepository;
import br.com.gubee.interview.core.mapper.PowerStatsRowMapper;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository("PowerStatsRepositoryPostgreImpl")
@RequiredArgsConstructor
public class PowerStatsRepositoryPostgreImpl implements PowerStatsRepository {

    private static final String CREATE_POWER_STATS_QUERY = "INSERT INTO power_stats" +
        " (strength, agility, dexterity, intelligence)" +
        " VALUES (:strength, :agility, :dexterity, :intelligence) RETURNING id";

    private static final String FIND_POWER_STATS_BY_UUID_QUERY = "SELECT *" +
            " FROM power_stats" +
            " WHERE id = :uuid";

    private static final String UPDATE_POWER_STATS_BY_ID_QUERY = "UPDATE power_stats" +
            " SET strength = :strength," +
            " agility = :agility," +
            " dexterity = :dexterity," +
            " intelligence = :intelligence," +
            " updated_at = now()" +
            " WHERE id = :id";

    private static final String DELETE_POWER_STATS_BY_ID_QUERY = "DELETE FROM power_stats" +
            " WHERE id = :id";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public UUID create(PowerStats powerStats) {
        return namedParameterJdbcTemplate.queryForObject(
            CREATE_POWER_STATS_QUERY,
            new BeanPropertySqlParameterSource(powerStats),
            UUID.class);
    }

    @Override
    public PowerStats findById(UUID powerStatsId) {
        final Map<String, Object> params = Map.of("uuid",powerStatsId);

        PowerStats powerStats;

        try {
            powerStats = namedParameterJdbcTemplate.queryForObject(
                    FIND_POWER_STATS_BY_UUID_QUERY,
                    params,
                    new PowerStatsRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new RuntimeException("More than one PowerStats was found.");
        }

        return powerStats;
    }

    @Override
    public void update(PowerStats powerStats, UpdateHeroRequest updateHeroRequest) {
        PowerStats modifiedPowerStats = changeFields(powerStats, updateHeroRequest);

        final Map<String, Object> params = Map.of("strength", modifiedPowerStats.getStrength(),
                "agility", modifiedPowerStats.getAgility(),
                "dexterity", modifiedPowerStats.getDexterity(),
                "intelligence", modifiedPowerStats.getIntelligence(),
                "id", modifiedPowerStats.getId());

        namedParameterJdbcTemplate.update(
                UPDATE_POWER_STATS_BY_ID_QUERY,
                params
        );
    }

    @Override
    public void delete(UUID powerStatsId) {
        final Map<String, Object> params = Map.of("id", powerStatsId);

        namedParameterJdbcTemplate.update(
                DELETE_POWER_STATS_BY_ID_QUERY,
                params
        );
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

        return powerStats;
    }
}
