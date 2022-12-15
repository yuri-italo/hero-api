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
    private final String INVALID_HERO_NAME = "invalid_hero_name";

    @BeforeEach
    public void setUp() {
        dbCleaner();
    }

    @Test
    public void createDeveriaCriarHeroiSeTodasPropriedadesObrigatoriasForemPassadas() {
        // given
        CreateHeroRequest heroRequest = createHeroRequest();

        // when
        UUID uuid = heroService.create(heroRequest);
        Optional<Hero> heroOptional = heroService.findById(uuid);
        Hero createdHero = heroOptional.get();
        PowerStats createdPowerStats = powerStatsService.findById(createdHero.getPowerStatsId());

        // then
        Assertions.assertNotNull(createdHero);
        Assertions.assertEquals(heroRequest.getName(), createdHero.getName());
        Assertions.assertEquals(heroRequest.getRace(), createdHero.getRace());
        Assertions.assertEquals(heroRequest.getAgility(), createdPowerStats.getAgility());
        Assertions.assertEquals(heroRequest.getStrength(), createdPowerStats.getStrength());
        Assertions.assertEquals(heroRequest.getDexterity(), createdPowerStats.getDexterity());
        Assertions.assertEquals(heroRequest.getIntelligence(), createdPowerStats.getIntelligence());
    }

    @Test
    public void createDeveriaLancarExceptionSeFaltarPropriedadesObrigatorias() {
        // given
        CreateHeroRequest heroRequestWithMissingArgument = createHeroRequestWithMissingArgument();

        // when
        NullPointerException thrown = Assertions.assertThrows(NullPointerException.class, () -> heroService.create(heroRequestWithMissingArgument));

        // then
        Assertions.assertNotNull(thrown);
    }

    @Test
    public void findByIdDeveriaRetonarOptionalDeHeroiSeIdExistir() {
        // given
        UUID uuid = heroService.create(createHeroRequest());

        // when
        Optional<Hero> heroOptional = heroService.findById(uuid);
        Hero requestedHero = heroOptional.get();

        // then
        Assertions.assertNotNull(requestedHero);
        Assertions.assertEquals(uuid, requestedHero.getId());
    }

    @Test
    public void findByIdDeveriaRetornarEmptyOptionalSeIdNaoExistir() {
        // given
        UUID uuid = UUID.randomUUID();

        // when
        Optional<Hero> heroOptional = heroService.findById(uuid);

        // then
        Assertions.assertEquals(heroOptional, Optional.empty());
    }

    @Test
    public void findManyByNameDeveriaRetornarListaCom2HeroisContendoABuscaNoNome() {
        // given
        String search = "man";
        heroService.create(createSuperManRequest());
        heroService.create(createHeroRequest());

        // when
        List<Hero> heroes = heroService.findManyByName(search);
        String name1 = heroes.get(0).getName();
        String name2 = heroes.get(1).getName();

        // then
        Assertions.assertEquals(2, heroes.size());
        Assertions.assertTrue(name1.contains(search));
        Assertions.assertTrue(name2.contains(search));
    }

    @Test
    public void findManyByNameDeveriaRetornarListaVaziaSeNenhumHeroiConterABuscaNoNome() {
        // given
        UUID uuidSuperMan = heroService.create(createSuperManRequest());
        UUID uuidBatman = heroService.create(createHeroRequest());
        String name1 = heroService.findById(uuidSuperMan).get().getName();
        String name2 = heroService.findById(uuidBatman).get().getName();

        // when
        List<Hero> heroes = heroService.findManyByName(INVALID_HERO_NAME);

        // then
        Assertions.assertEquals(0, heroes.size());
        Assertions.assertFalse(name1.contains(INVALID_HERO_NAME));
        Assertions.assertFalse(name2.contains(INVALID_HERO_NAME));
    }

    @Test
    public void findByNameDeveriaRetornarOptionalDeHeroSeNomeForIgualAPesquisa() {
        // given
        String search = "Batman";
        heroService.create(createHeroRequest());

        // when
        Optional<Hero> heroOptional = heroService.findByName(search);
        Hero hero = heroOptional.get();

        // then
        Assertions.assertNotNull(hero);
        Assertions.assertEquals(search, hero.getName());
    }

    @Test
    public void findByNameDeveriaRetornarEmptyOptionalSeNomeForDiferenteDaPesquisa() {
        // given
        UUID uuid = heroService.create(createHeroRequest());
        Hero hero = heroService.findById(uuid).get();

        // when
        Optional<Hero> heroOptional = heroService.findByName(INVALID_HERO_NAME);

        // then
        Assertions.assertTrue(heroOptional.isEmpty());
        Assertions.assertNotEquals(INVALID_HERO_NAME, hero.getName());
    }

    @Test
    public void findAll_DeveriaRetornarListaSemNenhumHeroiCasoNaoTenhaHeroisCadastrados() {
        //given
        List<Hero> heroes = heroService.findAll();

        // then
        Assertions.assertNotNull(heroes);
        Assertions.assertEquals(0, heroes.size());
    }

    @Test
    public void findAll_DeveriaRetornarListComTodosHerois() {
        //given
        UUID heroId = heroService.create(createHeroRequest());
        UUID heroId2 = heroService.create(createSuperManRequest());

        // when
        List<Hero> heroes = heroService.findAll();

        // then
        Assertions.assertNotNull(heroes);
        Assertions.assertEquals(2, heroes.size());
        Assertions.assertEquals(heroId, heroes.get(0).getId());
        Assertions.assertEquals(heroId2, heroes.get(1).getId());
    }

    @Test
    public void update_DeveriaAtualizarDadosDoHeroi() {
        // given
        UUID uuid = heroService.create(createHeroRequest());
        Hero hero = heroService.findById(uuid).get();
        UpdatedHeroDTO updatedHeroDTO = createUpdatedHeroDTO();

        // when
        heroService.update(hero,updatedHeroDTO);
        PowerStats powerStats = powerStatsService.findById(hero.getPowerStatsId());

        // then
        Assertions.assertEquals(updatedHeroDTO.getName(), hero.getName());
        Assertions.assertEquals(updatedHeroDTO.getRace(), hero.getRace());
        Assertions.assertEquals(updatedHeroDTO.getEnabled(), hero.isEnabled());
        Assertions.assertEquals(updatedHeroDTO.getAgility(), powerStats.getAgility());
        Assertions.assertEquals(updatedHeroDTO.getDexterity(), powerStats.getDexterity());
        Assertions.assertEquals(updatedHeroDTO.getIntelligence(), powerStats.getIntelligence());
        Assertions.assertEquals(updatedHeroDTO.getStrength(), powerStats.getStrength());
    }

    @Test
    public void deleteDeveriaDeletarHeroiSeIdExistir() {
        // given
        UUID uuid = heroService.create(createHeroRequest());
        Hero hero = heroService.findById(uuid).get();

        // when
        heroService.delete(hero);
        Optional<Hero> heroOptional = heroService.findById(uuid);

        // then
        Assertions.assertTrue(heroOptional.isEmpty());
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
    private CreateHeroRequest createSuperManRequest() {
        return CreateHeroRequest.builder()
                .name("Superman")
                .agility(10)
                .dexterity(10)
                .strength(10)
                .intelligence(10)
                .race(Race.DIVINE)
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
                .enabled(false)
                .build();
    }
    private void dbCleaner() {
        heroService.findAll().forEach(h -> heroService.delete(h));
    }
}
