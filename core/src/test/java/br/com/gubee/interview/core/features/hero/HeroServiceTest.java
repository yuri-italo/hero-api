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

    @BeforeEach
    public void setUp() {
        dbCleaner();
        heroResponseUUID = heroService.create(createHeroRequest());
        heroResponseOptional = heroService.findById(heroResponseUUID);
    }

    @Test
    public void create_CreateHero_IfAllRequiredArguments() {
        Assertions.assertNotNull(heroResponseUUID);
    }

    @Test
    public void create_FailToCreateHero_IfMissingRequiredArgument() {
        Assertions.assertThrows(
                NullPointerException.class, () -> heroService.create(createHeroRequestWithMissingArgument())
        );
    }

    @Test
    public void findById_ReturnAHero_IfUUIDExist() {
        heroResponseOptional = heroService.findById(heroResponseUUID);
        Assertions.assertNotNull(heroResponseOptional.get());
    }

    @Test
    public void findById_ReturnEmpty_IfUUIDDoNotExist() {
        do {
            heroResponseOptional = heroService.findById(UUID.randomUUID());
        } while (heroResponseOptional.isPresent());

        Assertions.assertEquals(heroResponseOptional,Optional.empty());
    }

    @Test
    public void findManyByName_ReturnOneOrManyHeroes_IfNameExists() {
        List<Hero> heroList = heroService.findManyByName(heroResponseOptional.get().getName());
        Assertions.assertTrue(heroList.size() > 0);
    }

    @Test
    public void findManyByName_ReturnEmptyList_IfNameNotExists() {
        List<Hero> heroList = heroService.findManyByName("invalid_hero_name");
        Assertions.assertEquals(0, heroList.size());
    }

    @Test
    public void findByName_ReturnAHero_IfNameExists() {
        Optional<Hero> hero = heroService.findByName(heroResponseOptional.get().getName());
        Assertions.assertTrue(hero.isPresent());
    }

    @Test
    public void findByName_ReturnEmpty_IfNameNotExists() {
        Assertions.assertTrue(heroService.findByName("invalid_hero_name").isEmpty());
    }

    @Test
    public void findAll_Return_IfExistsHeroesOrNot() {
        Assertions.assertTrue(heroService.findAll().size() >= 0);
    }

    @Test
    public void update_UpdateHero_IfZeroOrMoreRequiredArgumentIsPresent() {
        Hero hero = heroResponseOptional.get();
        UpdatedHeroDTO updatedHeroDTO = createUpdatedHeroDTO();

        heroService.update(hero,updatedHeroDTO);

        PowerStats powerStats = powerStatsService.findById(hero.getPowerStatsId());

        if (updatedHeroDTO.getName() != null)
            Assertions.assertEquals(hero.getName(),updatedHeroDTO.getName());
        if (updatedHeroDTO.getRace() != null)
            Assertions.assertEquals(hero.getRace(),updatedHeroDTO.getRace());
        if (updatedHeroDTO.getAgility() != null)
            Assertions.assertEquals(powerStats.getAgility(),updatedHeroDTO.getAgility());
        if (updatedHeroDTO.getDexterity() != null)
            Assertions.assertEquals(powerStats.getDexterity(),updatedHeroDTO.getDexterity());
        if (updatedHeroDTO.getIntelligence() != null)
            Assertions.assertEquals(powerStats.getIntelligence(),updatedHeroDTO.getIntelligence());
        if (updatedHeroDTO.getStrength() != null)
            Assertions.assertEquals(powerStats.getStrength(),updatedHeroDTO.getStrength());
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
    private void dbCleaner() {
        heroService.findAll().forEach(h -> heroService.delete(h));
    }
}
