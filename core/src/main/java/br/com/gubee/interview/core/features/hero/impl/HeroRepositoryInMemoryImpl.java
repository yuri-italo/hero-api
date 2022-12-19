package br.com.gubee.interview.core.features.hero.impl;

import br.com.gubee.interview.core.features.hero.HeroRepository;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@NoRepositoryBean
public class HeroRepositoryInMemoryImpl implements HeroRepository {
    public static Map<UUID,Hero> heroStorage = new TreeMap<>();
    @Override
    public UUID create(Hero hero) {
        if (hero.getId() != null || hero.getName() == null)
            throw new IllegalArgumentException();

        for(Map.Entry<UUID,Hero> entry : heroStorage.entrySet())
            if (entry.getValue().equals(hero))
                throw new IllegalArgumentException();

        UUID uuid = UUID.randomUUID();
        hero.setId(uuid);
        Instant now = Instant.now();
        hero.setCreatedAt(now);
        hero.setUpdatedAt(now);
        hero.setEnabled(true);

        heroStorage.put(uuid,hero);

        return uuid;
    }

    @Override
    public Optional<Hero> findById(UUID uuid) {
        return Optional.ofNullable(heroStorage.get(uuid));
    }

    @Override
    public List<Hero> findManyByName(String search) {
        List<Hero> heroes = new ArrayList<>();

        for(Map.Entry<UUID,Hero> entry : heroStorage.entrySet())
            if (entry.getValue().getName().contains(search))
                heroes.add(entry.getValue());

        return heroes;
    }

    @Override
    public Optional<Hero> findByName(String search) {
        Hero hero = null;

        for(Map.Entry<UUID,Hero> entry : heroStorage.entrySet())
           if (entry.getValue().getName().equals(search))
               hero = entry.getValue();

        return Optional.ofNullable(hero);
    }

    @Override
    public List<Hero> findAll() {
        List<Hero> heroes = new ArrayList<>();

        for(Map.Entry<UUID,Hero> entry : heroStorage.entrySet())
            heroes.add(entry.getValue());

        return heroes;
    }

    @Override
    public void update(Hero hero, UpdateHeroRequest updateHeroRequest) {
        if (hero == null || updateHeroRequest == null)
            throw new NullPointerException();

        Hero modifiedhero = changeFields(hero, updateHeroRequest);
        heroStorage.put(hero.getId(),modifiedhero);
    }

    @Override
    public void delete(Hero hero) {
        if (hero == null)
            throw new NullPointerException();

        heroStorage.remove(hero.getId());
    }

    private Hero changeFields(Hero hero, UpdateHeroRequest updateHeroRequest) {
        if (updateHeroRequest.getName() != null && !(hero.getName().equals(updateHeroRequest.getName())))
            hero.setName(updateHeroRequest.getName());

        if (updateHeroRequest.getRace() != null && !(hero.getRace() == updateHeroRequest.getRace()))
            hero.setRace(updateHeroRequest.getRace());

        if (updateHeroRequest.getEnabled() != null && !(hero.isEnabled() == updateHeroRequest.getEnabled()))
            hero.setEnabled(updateHeroRequest.getEnabled());

        hero.setUpdatedAt(Instant.now());

        return hero;
    }
}
