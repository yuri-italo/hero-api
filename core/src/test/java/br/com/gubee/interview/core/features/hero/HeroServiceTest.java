package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.hero.impl.HeroRepositoryInMemoryImpl;
import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.core.features.powerstats.impl.PowerStatsRepositoryInMemoryImpl;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
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

class HeroServiceTest {

    private HeroRepositoryInMemoryImpl heroRepositoryInMemory;
    private PowerStatsService powerStatsService;
    private HeroService heroService;

    @BeforeEach
    void init() {
        heroRepositoryInMemory = new HeroRepositoryInMemoryImpl();
        PowerStatsRepositoryInMemoryImpl powerStatsRepositoryInMemory = new PowerStatsRepositoryInMemoryImpl();
        powerStatsService = new PowerStatsService(powerStatsRepositoryInMemory);
        heroService= new HeroService(heroRepositoryInMemory,powerStatsService);

        storageCleaner();
    }

    @Test
    void createShouldCreateANewHeroContainingTheRequestProperties() {
        // given
        CreateHeroRequest request = createHeroRequest();

        // when
        UUID uuid = heroService.create(request);

        // then
        assertNotNull(uuid);

        Optional<Hero> optionalHero = heroRepositoryInMemory.findById(uuid);
        assertTrue(optionalHero.isPresent());

        Hero createdHero = optionalHero.get();
        assertNotNull(createdHero.getId());
        assertEquals(request.getName(), createdHero.getName());
        assertEquals(request.getRace(), createdHero.getRace());
        assertTrue(createdHero.isEnabled());
        assertNotNull(createdHero.getCreatedAt());
        assertNotNull(createdHero.getUpdatedAt());
        assertNotNull(createdHero.getPowerStatsId());

        PowerStats createdPowerStats = powerStatsService.findById(createdHero.getPowerStatsId());
        assertNotNull(createdPowerStats);
        assertNotNull(createdPowerStats.getId());
        assertEquals(request.getAgility(),createdPowerStats.getAgility());
        assertEquals(request.getDexterity(),createdPowerStats.getDexterity());
        assertEquals(request.getIntelligence(),createdPowerStats.getIntelligence());
        assertEquals(request.getStrength(),createdPowerStats.getStrength());
        assertNotNull(createdPowerStats.getCreatedAt());
        assertNotNull(createdPowerStats.getUpdatedAt());
    }

    @Test
    void createShouldThrowIllegalArgumentExceptionWhenTryingToCreateTwoHeroesWithSameName() {
        // given
        heroService.create(createHeroRequest());

        // when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> heroService.create(createHeroRequest()));

        // then
        assertNotNull(e);
    }

    @Test
    void createShouldThrowIllegalArgumentExceptionWhenRequestNameNotExists() {
        // given
        CreateHeroRequest request = createWithoutNameHeroRequest();

        // when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> heroService.create(request));

        // then
        assertNotNull(e);
    }

    @Test
    void findByIdShouldReturnAnOptionalWithTheCorrectHeroWhenIdExists() {
        // given
        CreateHeroRequest request = createHeroRequest();

        UUID uuid = heroService.create(request);

        // when
        Optional<Hero> optionalHero = heroService.findById(uuid);

        // then
        assertTrue(optionalHero.isPresent());

        Hero createdHero = optionalHero.get();
        assertNotNull(createdHero.getId());
        assertEquals(request.getName(), createdHero.getName());
        assertEquals(request.getRace(), createdHero.getRace());
        assertTrue(createdHero.isEnabled());
        assertNotNull(createdHero.getCreatedAt());
        assertNotNull(createdHero.getUpdatedAt());
        assertNotNull(createdHero.getPowerStatsId());

        PowerStats createdPowerStats = powerStatsService.findById(createdHero.getPowerStatsId());
        assertNotNull(createdPowerStats);
        assertNotNull(createdPowerStats.getId());
        assertEquals(request.getAgility(),createdPowerStats.getAgility());
        assertEquals(request.getDexterity(),createdPowerStats.getDexterity());
        assertEquals(request.getIntelligence(),createdPowerStats.getIntelligence());
        assertEquals(request.getStrength(),createdPowerStats.getStrength());
        assertNotNull(createdPowerStats.getCreatedAt());
        assertNotNull(createdPowerStats.getUpdatedAt());
    }

    @Test
    void findByIdShouldReturnAnEmptyOptionalWhenIdNotExists() {
        // given
        UUID uuid = UUID.randomUUID();

        // when
        Optional<Hero> optionalHero = heroService.findById(uuid);

        // then
        assertTrue(optionalHero.isEmpty());
    }

    @Test
    void findManyByNameShouldReturnAListWithTwoHeroesContainingTheSearchInTheName() {
        // given
        UUID uuid = heroService.create(createHeroRequest());
        UUID uuid2 = heroService.create(createAnotherHeroRequest());

        String search = "man";

        // when
        List<Hero> heroes = heroService.findManyByName(search);

        // then
        assertNotNull(heroes);
        assertEquals(2,heroes.size());

        Optional<Hero> optionalHero = heroService.findById(uuid);
        Hero hero = optionalHero.get();
        assertTrue(hero.getName().contains(search));
        assertTrue(heroes.contains(hero));

        Optional<Hero> optionalHero2 = heroService.findById(uuid2);
        Hero hero2 = optionalHero2.get();
        assertTrue(hero2.getName().contains(search));
        assertTrue(heroes.contains(hero2));
    }

    @Test
    void findManyByNameShouldReturnAnEmptyListWhenThereAreNoHeroesContainingTheSearchInTheName() {
        // given
        UUID uuid = heroService.create(createHeroRequest());
        UUID uuid2 = heroService.create(createAnotherHeroRequest());

        String search = "INVALID_SEARCH";

        // when
        List<Hero> heroes = heroService.findManyByName(search);

        // then
        assertNotNull(heroes);
        assertEquals(0,heroes.size());
    }

    @Test
    void findByNameShouldReturnAnOptionalWithHeroWhenNameEqualsSearch() {
        // given
        heroService.create(createHeroRequest());

        String search = "Batman";

        // when
        Optional<Hero> optionalHero = heroService.findByName(search);

        // then
        assertTrue(optionalHero.isPresent());

        Hero hero = optionalHero.get();
        assertTrue(hero.getName().equalsIgnoreCase(search));
    }

    @Test
    void findByNameShouldReturnAnEmptyOptionalWhenSearchDoesNotEqualAnyHeroName() {
        // given
        heroService.create(createHeroRequest());

        String search = "Spider";

        // when
        Optional<Hero> optionalHero = heroService.findByName(search);

        // then
        assertTrue(optionalHero.isEmpty());
    }

    @Test
    void findAllShouldReturnAListWithTwoCreatedHeroes() {
        // given
        UUID uuid = heroService.create(createHeroRequest());
        UUID uuid2 = heroService.create(createAnotherHeroRequest());

        // when
        List<Hero> heroes = heroService.findAll();

        // then
        assertNotNull(heroes);
        assertEquals(2,heroes.size());

        Hero hero = heroService.findById(uuid).get();
        Hero hero2 = heroService.findById(uuid2).get();
        assertTrue(heroes.contains(hero));
        assertTrue(heroes.contains(hero2));
    }

    @Test
    void findAllShouldReturnAnEmptyListWhenDoesNotExistsCreatedHeroes() {
        // when
        List<Hero> heroes = heroService.findAll();

        // then
        assertNotNull(heroes);
        assertEquals(0,heroes.size());
    }

    @Test
    void updateShouldUpdateHeroFieldsAccordingToRequest() {
        // given
        UpdateHeroRequest updateHeroRequest = createUpdateHeroRequest();

        UUID uuid = heroService.create(createHeroRequest());

        Optional<Hero> optionalHero = heroService.findById(uuid);
        Hero hero = optionalHero.get();
        Instant heroCreateTimeBeforeUpdate = hero.getCreatedAt();
        Instant heroUpdateTimeBeforeUpdate = hero.getUpdatedAt();

        PowerStats powerStats = powerStatsService.findById(hero.getPowerStatsId());
        Instant powerStatsCreateTimeBeforeUpdate = powerStats.getCreatedAt();
        Instant powerStatsUpdateTimeBeforeUpdate = powerStats.getUpdatedAt();

        // when
        heroService.update(hero,updateHeroRequest);

        // then
        assertNotNull(hero.getId());
        assertEquals(updateHeroRequest.getName(),hero.getName());
        assertEquals(updateHeroRequest.getRace(),hero.getRace());
        assertEquals(updateHeroRequest.getEnabled(),hero.isEnabled());
        assertEquals(heroCreateTimeBeforeUpdate,hero.getCreatedAt());
        assertNotEquals(heroUpdateTimeBeforeUpdate,hero.getUpdatedAt());
        assertNotNull(hero.getPowerStatsId());

        assertNotNull(powerStats.getId());
        assertEquals(updateHeroRequest.getAgility(),powerStats.getAgility());
        assertEquals(updateHeroRequest.getDexterity(),powerStats.getDexterity());
        assertEquals(updateHeroRequest.getIntelligence(),powerStats.getIntelligence());
        assertEquals(updateHeroRequest.getStrength(),powerStats.getStrength());
        assertEquals(powerStatsCreateTimeBeforeUpdate,powerStats.getCreatedAt());
        assertNotEquals(powerStatsUpdateTimeBeforeUpdate,powerStats.getUpdatedAt());
    }

    @Test
    void deleteShouldDeleteWhenHeroExists() {
        // given
        UUID uuid = heroService.create(createHeroRequest());
        Optional<Hero> optionalHero = heroService.findById(uuid);
        Hero hero = optionalHero.get();

        // when
        heroService.delete(hero);

        // then
        Optional<Hero> deletedHero = heroService.findById(uuid);
        assertTrue(deletedHero.isEmpty());

        PowerStats deletedPowerStats = powerStatsService.findById(hero.getPowerStatsId());
        assertNull(deletedPowerStats);
    }

    @Test
    void deleteShouldThrowNullPointerExceptionWhenHeroIsNull() {
        // given

        Hero hero = null;

        // when
        NullPointerException e = assertThrows(NullPointerException.class, () -> heroService.delete(hero));

        // then
        assertNotNull(e);
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
                .agility(10)
                .dexterity(10)
                .strength(10)
                .intelligence(10)
                .race(Race.DIVINE)
                .build();
    }

    private CreateHeroRequest createWithoutNameHeroRequest() {
        return CreateHeroRequest.builder()
                .agility(9)
                .dexterity(9)
                .strength(9)
                .intelligence(9)
                .race(Race.ALIEN)
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

    private void storageCleaner() {
        heroRepositoryInMemory.findAll()
                .forEach(hero -> heroRepositoryInMemory.delete(hero));
    }
}