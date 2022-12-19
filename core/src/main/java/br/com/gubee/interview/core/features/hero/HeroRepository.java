package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface HeroRepository {
    UUID create(Hero hero);
    Optional<Hero> findById(UUID uuid);
    List<Hero> findManyByName(String heroName);
    Optional<Hero> findByName(String heroName);
    List<Hero> findAll();
    void update(Hero hero, UpdateHeroRequest updateHeroRequest);
    void delete(Hero hero);
}
