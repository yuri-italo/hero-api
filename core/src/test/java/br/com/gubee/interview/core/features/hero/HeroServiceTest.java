package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.dto.UpdatedHeroDTO;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HeroServiceTest {

    @Autowired
    private HeroService heroService;
    @Autowired
    private PowerStatsService powerStatsService;
    private UUID heroResponseUUID;
    private Optional<Hero> heroResponseOptional;

    private final String INVALID_HERO_NAME = "invalid_hero_name";

    @BeforeEach
    public void setUp() {
        dbCleaner();
        heroResponseUUID = heroService.create(createHeroRequest());
        heroResponseOptional = heroService.findById(heroResponseUUID);
    }

    @Test
    public void create_CreateHero_IfAllRequiredArguments() {
        Assertions.assertNotNull(heroResponseOptional.get().getId());
        Assertions.assertNotNull(heroResponseOptional.get().getName());
        Assertions.assertNotNull(heroResponseOptional.get().getRace());
        Assertions.assertNotNull(heroResponseOptional.get().getPowerStatsId());
        Assertions.assertNotNull(heroResponseOptional.get().getCreatedAt());
        Assertions.assertNotNull(heroResponseOptional.get().getUpdatedAt());
        Assertions.assertTrue(heroResponseOptional.get().isEnabled());
    }

    @Test
    public void create_FailToCreateHero_IfMissingRequiredArgument() {
        Assertions.assertThrows(
                NullPointerException.class, () -> heroService.create(createHeroRequestWithMissingArgument())
        );
    }

    @Test
    public void findById_ReturnOptionalOfHero_IfUUIDExist() {
        Optional<Hero> optionalHero = heroService.findById(heroResponseUUID);
        Hero hero = optionalHero.get();

        Assertions.assertTrue(optionalHero.isPresent());
        Assertions.assertEquals(Hero.class,hero.getClass());
        Assertions.assertNotNull(hero.getId());
        Assertions.assertNotNull(hero.getName());
        Assertions.assertNotNull(hero.getRace());
        Assertions.assertNotNull(hero.getPowerStatsId());
        Assertions.assertNotNull(hero.getCreatedAt());
        Assertions.assertNotNull(hero.getUpdatedAt());
    }

    @Test
    public void findById_ReturnEmpty_IfUUIDDoNotExist() {
        heroResponseOptional = heroService.findById(UUID.randomUUID());
        Assertions.assertEquals(heroResponseOptional,Optional.empty());
    }

    @Test
    public void findManyByName_ReturnOneOrManyHeroes_IfNameExists() {
        List<Hero> heroList = heroService.findManyByName(heroResponseOptional.get().getName());

        Assertions.assertTrue(heroList.size() > 0);
        Assertions.assertEquals(Hero.class,heroList.get(0).getClass());
    }

    @Test
    public void findManyByName_ReturnEmptyList_IfNameNotExists() {
        List<Hero> heroList = heroService.findManyByName(INVALID_HERO_NAME);
        Assertions.assertEquals(0, heroList.size());
    }

    @Test
    public void findByName_ReturnOptionalOfHero_IfNameExists() {
        Optional<Hero> optionalHero = heroService.findByName(heroResponseOptional.get().getName());
        Hero hero = optionalHero.get();

        Assertions.assertTrue(optionalHero.isPresent());
        Assertions.assertEquals(Hero.class,hero.getClass());
        Assertions.assertNotNull(hero.getId());
        Assertions.assertNotNull(hero.getName());
        Assertions.assertNotNull(hero.getRace());
        Assertions.assertNotNull(hero.getPowerStatsId());
        Assertions.assertNotNull(hero.getCreatedAt());
        Assertions.assertNotNull(hero.getUpdatedAt());
    }

    @Test
    public void findByName_ReturnEmpty_IfNameNotExists() {
        Optional<Hero> optionalHero = heroService.findByName(INVALID_HERO_NAME);
        Assertions.assertTrue(optionalHero.isEmpty());
    }

    @Test
    public void findAll_ReturnAList_IfExistsHeroesOrNot() {
        List<Hero> heroes = heroService.findAll();
        Assertions.assertTrue(heroes.size() >= 0);
    }

    @Test
    public void update_UpdateHero_IfAllRequiredArgumentsArePresent() {
        Hero hero = heroResponseOptional.get();
        UpdatedHeroDTO updatedHeroDTO = createUpdatedHeroDTO();
        heroService.update(hero,updatedHeroDTO);
        PowerStats powerStats = powerStatsService.findById(hero.getPowerStatsId());

        Assertions.assertEquals(hero.getName(),updatedHeroDTO.getName());
        Assertions.assertEquals(hero.getRace(),updatedHeroDTO.getRace());
        Assertions.assertEquals(powerStats.getAgility(),updatedHeroDTO.getAgility());
        Assertions.assertEquals(powerStats.getDexterity(),updatedHeroDTO.getDexterity());
        Assertions.assertEquals(powerStats.getIntelligence(),updatedHeroDTO.getIntelligence());
        Assertions.assertEquals(powerStats.getStrength(),updatedHeroDTO.getStrength());
        Assertions.assertEquals(UpdatedHeroDTO.class,updatedHeroDTO.getClass());
        Assertions.assertEquals(Hero.class,hero.getClass());
    }

    @Test
    public void update_UpdateHero_IfRequiredArgumentsAreMissing() {
        Hero hero = heroResponseOptional.get();
        UpdatedHeroDTO updatedHeroDTO = createUpdatedHeroDTO();
        heroService.update(hero,updatedHeroDTO);
        PowerStats powerStats = powerStatsService.findById(hero.getPowerStatsId());

        Assertions.assertEquals(hero.getRace(),updatedHeroDTO.getRace());
        Assertions.assertEquals(powerStats.getAgility(),updatedHeroDTO.getAgility());
        Assertions.assertEquals(powerStats.getIntelligence(),updatedHeroDTO.getIntelligence());
        Assertions.assertEquals(powerStats.getStrength(),updatedHeroDTO.getStrength());
        Assertions.assertEquals(UpdatedHeroDTO.class,updatedHeroDTO.getClass());
        Assertions.assertEquals(Hero.class,hero.getClass());
    }

    @Test
    public void delete_DeleteHero_IfHeroExists() {
        UUID heroId = heroResponseUUID;
        heroService.delete(heroResponseOptional.get());

        Assertions.assertEquals(heroService.findById(heroId),Optional.empty());
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

    private CreateHeroRequest createHeroRequestWithMissingArgument() {
        return CreateHeroRequest.builder()
                .agility(5)
                .dexterity(8)
                .strength(6)
                .intelligence(10)
                .race(Race.HUMAN)
                .build();
    }

    private UpdatedHeroDTO createUpdatedHeroDTO() {
        return UpdatedHeroDTO.builder()
                .name("Spider-Man")
                .agility(10)
                .dexterity(7)
                .strength(4)
                .intelligence(5)
                .race(Race.ALIEN)
                .build();
    }

    private UpdatedHeroDTO createUpdatedHeroDTOWithMissingArgument() {
        return UpdatedHeroDTO.builder()
                .agility(10)
                .strength(4)
                .intelligence(5)
                .race(Race.ALIEN)
                .build();
    }
    private void dbCleaner() {
        heroService.findAll().forEach(h -> heroService.delete(h));
    }
}
