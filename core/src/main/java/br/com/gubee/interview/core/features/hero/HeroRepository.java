package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.mapper.HeroRowMapper;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.dto.UpdatedHeroDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HeroRepository {

    private static final String CREATE_HERO_QUERY = "INSERT INTO hero" +
        " (name, race, power_stats_id)" +
        " VALUES (:name, :race, :powerStatsId) RETURNING id";

    private static final String FIND_HERO_BY_UUID_QUERY = "SELECT *" +
            " FROM hero" +
            " WHERE id = :uuid";

    private static final String FIND_HERO_BY_NAME_QUERY = "SELECT *" +
            " FROM hero" +
            " WHERE name ilike :heroName";

    private static final String UPDATE_HERO_BY_ID_QUERY = "UPDATE hero" +
            " SET name = :name, race = :race, enabled = :enabled, updated_at = now()" +
            " WHERE id = :id";

    private static final String DELETE_HERO_QUERY = "DELETE FROM hero" +
            " WHERE id = :id";

    private static final String FIND_ALL_HEROES_QUERY = "SELECT *" +
            " FROM hero";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    UUID create(Hero hero) {
        final Map<String, Object> params = Map.of("name", hero.getName(),
            "race", hero.getRace().name(),
            "powerStatsId", hero.getPowerStatsId());

        return namedParameterJdbcTemplate.queryForObject(
            CREATE_HERO_QUERY,
            params,
            UUID.class);
    }

    Optional<Hero> findById(UUID uuid) {
        final Map<String, Object> params = Map.of("uuid",uuid);

        Hero hero;

        try {
            hero = namedParameterJdbcTemplate.queryForObject(
                    FIND_HERO_BY_UUID_QUERY,
                    params,
                    new HeroRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new RuntimeException("More than one Hero was found.");
        }

        return Optional.ofNullable(hero);
    }

    public List<Hero> findManyByName(String heroName) {
        final Map<String, Object> params = Map.of("heroName","%" + heroName + "%");

        return namedParameterJdbcTemplate.query(
                FIND_HERO_BY_NAME_QUERY,
                params,
                new HeroRowMapper()
        );
    }

    public Optional<Hero> findByName(String heroName) {

        final Map<String, Object> params = Map.of("heroName",heroName);

        Hero hero;

        try {
            hero = namedParameterJdbcTemplate.queryForObject(
                    FIND_HERO_BY_NAME_QUERY,
                    params,
                    new HeroRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new RuntimeException("More than one Hero was found.");
        }

        return Optional.ofNullable(hero);
    }

    public List<Hero> findAll() {
        return namedParameterJdbcTemplate.query(
                FIND_ALL_HEROES_QUERY,
                new HeroRowMapper()
        );
    }

    public void update(Hero hero, UpdatedHeroDTO updateHeroDTO) {
        Hero modifiedHero = changeFields(hero, updateHeroDTO);

        final Map<String, Object> params = Map.of("name", modifiedHero.getName(),
                "race", modifiedHero.getRace().name(),
                "enabled", modifiedHero.isEnabled(),
                "id", modifiedHero.getId());

        namedParameterJdbcTemplate.update(
                UPDATE_HERO_BY_ID_QUERY,
                params
        );
    }

    public void delete(Hero hero) {
        final Map<String, Object> params = Map.of("id", hero.getId());

        namedParameterJdbcTemplate.update(
                DELETE_HERO_QUERY,
                params
        );
    }

    private Hero changeFields(Hero hero, UpdatedHeroDTO updateHeroDTO) {
        if (updateHeroDTO.getName() != null && !hero.getName().equals(updateHeroDTO.getName()))
            hero.setName(updateHeroDTO.getName());
        if (updateHeroDTO.getRace() != null && !hero.getRace().equals(updateHeroDTO.getRace()))
            hero.setRace(updateHeroDTO.getRace());
        if (updateHeroDTO.getEnabled() != null && !hero.isEnabled() == updateHeroDTO.getEnabled())
            hero.setEnabled(updateHeroDTO.getEnabled());

        return hero;
    }
}
