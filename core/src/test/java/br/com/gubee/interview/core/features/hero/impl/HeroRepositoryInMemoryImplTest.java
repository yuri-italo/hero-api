package br.com.gubee.interview.core.features.hero.impl;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HeroRepositoryInMemoryImplTest {
    private HeroRepositoryInMemoryImpl heroRepositoryInMemory;

    @BeforeEach
    void init() {
        heroStorageCleaner();
        heroRepositoryInMemory = new HeroRepositoryInMemoryImpl();
    }

    @Test
    void createShouldCreateHeroWhenAllArgumentsArePresent() {
        // given
        CreateHeroRequest request = createHeroRequest();
        Hero hero = createHero(request);

        // when
        UUID heroId = heroRepositoryInMemory.create(hero);

        // then
        Hero createdHero = HeroRepositoryInMemoryImpl.heroStorage.get(heroId);
        assertNotNull(createdHero);

        assertEquals(request.getName(),createdHero.getName());
        assertEquals(request.getRace(),createdHero.getRace());
        assertTrue(createdHero.isEnabled());
        assertNotNull(createdHero.getCreatedAt());
        assertNotNull(createdHero.getUpdatedAt());
        assertNotNull(createdHero.getPowerStatsId());
    }

    @Test
    void createShouldThrowIllegalArgumentExceptionWhenTryingToCreateTwoHeroesWithSameName() {
        // given
        Hero hero = createHero(createHeroRequest());
        Hero hero2 = createHero(createHeroRequest());

        // when
        heroRepositoryInMemory.create(hero);
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class, () -> heroRepositoryInMemory.create(hero2)
        );

        // then
        assertNotNull(e);
    }

    @Test
    void createShouldThrowIllegalArgumentExceptionWhenHeroParameterHaveId() {
        // given
        Hero hero = createHero(createHeroRequest());
        hero.setId(UUID.randomUUID());

        // when
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class, () -> heroRepositoryInMemory.create(hero)
        );

        // then
        assertNotNull(e);
    }

    @Test
    void createShouldThrowIllegalArgumentExceptionWhenHeroParameterNameNotExists() {
        // given
        Hero hero = createHero(createHeroRequest());
        hero.setName(null);

        // when
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class, () -> heroRepositoryInMemory.create(hero)
        );

        // then
        assertNotNull(e);
    }

    @Test
    void findByIdShouldReturnAnOptionalContainingAHeroWhenIdExists() {
        // given
        Hero hero = createHero(createHeroRequest());

        heroRepositoryInMemory.create(hero);

        // when
        Optional<Hero> heroOptional = heroRepositoryInMemory.findById(hero.getId());

        // then
        assertTrue(heroOptional.isPresent());

        Hero heroById = heroOptional.get();
        assertEquals(hero.getId(),heroById.getId());
        assertEquals(hero.getName(),heroById.getName());
        assertEquals(hero.getRace(),heroById.getRace());
        assertEquals(hero.getCreatedAt(),heroById.getCreatedAt());
        assertEquals(hero.getUpdatedAt(),heroById.getUpdatedAt());
        assertEquals(hero.getPowerStatsId(),heroById.getPowerStatsId());
    }

    @Test
    void findByIdShouldReturnAnEmptyOptionalWhenIdNotExists() {
        // given
        UUID uuid = UUID.randomUUID();

        // when
        Optional<Hero> heroOptional = heroRepositoryInMemory.findById(uuid);

        // then
        assertTrue(heroOptional.isEmpty());
    }

    @Test
    void findManyByNameShouldReturnAListContainingTwoHeroesWithManInTheName() {
        // given
        Hero hero = createHero(createHeroRequest());
        Hero hero2 = createHero(createAnotherHeroRequest());

        String search = "man";

        heroRepositoryInMemory.create(hero);
        heroRepositoryInMemory.create(hero2);

        // when
        List<Hero> heroesContainingSearch = heroRepositoryInMemory.findManyByName(search);

        // then
        assertNotNull(heroesContainingSearch);
        assertEquals(2,heroesContainingSearch.size());
        assertTrue(heroesContainingSearch.contains(hero));
        assertTrue(heroesContainingSearch.contains(hero2));
    }

    @Test
    void findManyByNameShouldReturnAnEmptyListWhenThereAreNoHeroesContainingTheSearchInTheName() {
        // given
        String search = "INVALID_SEARCH";

        // when
        List<Hero> heroesContainingSearch = heroRepositoryInMemory.findManyByName(search);

        // then
        assertNotNull(heroesContainingSearch);
        assertEquals(0,heroesContainingSearch.size());
    }

    @Test
    void findByNameShouldReturnAnOptionalContainingAHeroWhenParameterNameExists() {
        // given
        Hero hero = createHero(createHeroRequest());
        heroRepositoryInMemory.create(hero);

        // when
        Optional<Hero> heroOptional = heroRepositoryInMemory.findByName(hero.getName());

        // then
        assertTrue(heroOptional.isPresent());

        Hero heroByName = heroOptional.get();
        assertEquals(hero.getId(),heroByName.getId());
        assertEquals(hero.getName(),heroByName.getName());
        assertEquals(hero.getRace(),heroByName.getRace());
        assertEquals(hero.getCreatedAt(),heroByName.getCreatedAt());
        assertEquals(hero.getUpdatedAt(),heroByName.getUpdatedAt());
        assertEquals(hero.getPowerStatsId(),heroByName.getPowerStatsId());
    }


    @Test
    void findByNameShouldReturnAnEmptyOptionalWhenParameterNameNotExists() {
        // given
        String search = "INVALID_HERO_NAME";

        // when
        Optional<Hero> heroOptional = heroRepositoryInMemory.findByName(search);

        // then
        assertTrue(heroOptional.isEmpty());
    }

    @Test
    void findAllShouldReturnAListWithAllHeroesCreated() {
        // given
        Hero hero = createHero(createHeroRequest());
        Hero hero2 = createHero(createAnotherHeroRequest());

        heroRepositoryInMemory.create(hero);
        heroRepositoryInMemory.create(hero2);

        // when
        List<Hero> allHeroes = heroRepositoryInMemory.findAll();

        // then
        assertEquals(2, allHeroes.size());
        assertTrue(allHeroes.contains(hero));
        assertTrue(allHeroes.contains(hero2));
    }

    @Test
    void findAllShouldReturnAnEmptyListWhenThereAreNoHeroesCreated() {
        // when
        List<Hero> allHeroes = heroRepositoryInMemory.findAll();

        // then
        assertEquals(0, allHeroes.size());
    }

    @Test
    void updateShouldUpdateHeroFieldsAccordingToRequestWhenIdExists() {
        // given
        Hero hero = createHero(createHeroRequest());
        UpdateHeroRequest updateHeroRequest = createUpdateHeroRequest();

        heroRepositoryInMemory.create(hero);

        Instant heroUpdateTimeBeforeUpdate = hero.getUpdatedAt();
        Instant heroCreateTimeBeforeUpdate = hero.getCreatedAt();

        // when
        heroRepositoryInMemory.update(hero,updateHeroRequest);

        // then
        assertEquals(updateHeroRequest.getName(),hero.getName());
        assertEquals(updateHeroRequest.getRace(),hero.getRace());
        assertEquals(updateHeroRequest.getEnabled(),hero.isEnabled());
        assertEquals(heroCreateTimeBeforeUpdate,hero.getCreatedAt());
        assertNotEquals(heroUpdateTimeBeforeUpdate,hero.getUpdatedAt());
        assertNotNull(hero.getPowerStatsId());
    }

    @Test
    void updateShouldThrowNullPointerExceptionWhenHeroIsNull() {
        // given
        Hero hero = null;
        UpdateHeroRequest updateHeroRequest = createUpdateHeroRequest();

        // when
        NullPointerException e = assertThrows(
                NullPointerException.class, () -> heroRepositoryInMemory.update(hero, updateHeroRequest)
        );

        // then
        assertNotNull(e);
    }

    @Test
    void updateShouldThrowNullPointerExceptionWhenUpdateRequestIsNull() {
        // given
        Hero hero = createHero(createHeroRequest());
        UpdateHeroRequest updateHeroRequest = null;

        // when
        NullPointerException e = assertThrows(
                NullPointerException.class, () -> heroRepositoryInMemory.update(hero, updateHeroRequest)
        );

        // then
        assertNotNull(e);
    }

    @Test
    void deleteShouldDeleteHeroWhenIdExists() {
        // given
        Hero hero = createHero(createHeroRequest());

        UUID uuid = heroRepositoryInMemory.create(hero);

        // when
        heroRepositoryInMemory.delete(hero);

        // then
        Hero deletedHero = HeroRepositoryInMemoryImpl.heroStorage.get(uuid);
        assertNull(deletedHero);
    }

    @Test
    void deleteShouldReturnNullPointerExceptionWhenHeroIsNull() {
        // given
        Hero hero = null;

        // when
        NullPointerException e = assertThrows(
                NullPointerException.class, () -> heroRepositoryInMemory.delete(hero)
        );

        // then
        assertNotNull(e);
    }

    private Hero createHero(CreateHeroRequest HeroRequest) {
        return new Hero(HeroRequest, UUID.randomUUID());
    }

    private CreateHeroRequest createHeroRequest() {
        return CreateHeroRequest.builder()
                .name("Batman")
                .agility(5)
                .dexterity(8)
                .strength(6)
                .intelligence(10)
                .race(Race.HUMAN)
                .build();
    }

    private CreateHeroRequest createAnotherHeroRequest() {
        return CreateHeroRequest.builder()
                .name("Superman")
                .agility(9)
                .dexterity(9)
                .strength(10)
                .intelligence(10)
                .race(Race.DIVINE)
                .build();
    }

    private UpdateHeroRequest createUpdateHeroRequest() {
        return UpdateHeroRequest.builder()
                .name("Thor")
                .race(Race.DIVINE)
                .agility(8)
                .dexterity(8)
                .intelligence(8)
                .strength(8)
                .enabled(false)
                .build();
    }

    private void heroStorageCleaner() {
        HeroRepositoryInMemoryImpl heroRepository = new HeroRepositoryInMemoryImpl();

        List<Hero> allHeroes = heroRepository.findAll();

        for (var hero : allHeroes)
            heroRepository.delete(hero);
    }
}