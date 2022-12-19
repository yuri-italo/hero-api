package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.hero.impl.HeroRepositoryInMemoryImpl;
import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.core.features.powerstats.impl.PowerStatsRepositoryInMemoryImpl;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HeroServiceTest {

    @Test
    void createShouldCreateANewHeroContainingTheRequestProperties() {
        // given
        HeroRepositoryInMemoryImpl heroRepositoryInMemory = new HeroRepositoryInMemoryImpl();
        PowerStatsRepositoryInMemoryImpl powerStatsRepositoryInMemory = new PowerStatsRepositoryInMemoryImpl();
        PowerStatsService powerStatsService = new PowerStatsService(powerStatsRepositoryInMemory);
        HeroService heroService = new HeroService(heroRepositoryInMemory,powerStatsService);
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
    void findByIdShouldReturnAnOptionalWithTheCorrectHeroWhenIdExists() {
        // given
        HeroRepositoryInMemoryImpl heroRepositoryInMemory = new HeroRepositoryInMemoryImpl();
        PowerStatsRepositoryInMemoryImpl powerStatsRepositoryInMemory = new PowerStatsRepositoryInMemoryImpl();
        PowerStatsService powerStatsService = new PowerStatsService(powerStatsRepositoryInMemory);
        HeroService heroService = new HeroService(heroRepositoryInMemory,powerStatsService);
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
    void findManyByNameShouldReturnAListWithTwoHeroesContainingTheSearchInTheName() {
        // given
        HeroRepositoryInMemoryImpl heroRepositoryInMemory = new HeroRepositoryInMemoryImpl();
        PowerStatsRepositoryInMemoryImpl powerStatsRepositoryInMemory = new PowerStatsRepositoryInMemoryImpl();
        PowerStatsService powerStatsService = new PowerStatsService(powerStatsRepositoryInMemory);
        HeroService heroService = new HeroService(heroRepositoryInMemory,powerStatsService);
        CreateHeroRequest request = createHeroRequest();
        CreateHeroRequest request2 = createAnotherHeroRequest();

        UUID uuid = heroService.create(request);
        UUID uuid2 = heroService.create(request2);

        String search = "man";

        // when
        List<Hero> heroes = heroService.findManyByName(search);

        // then
        assertNotNull(heroes);

    }

    @Test
    void findByName() {
    }

    @Test
    void findAll() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
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
}