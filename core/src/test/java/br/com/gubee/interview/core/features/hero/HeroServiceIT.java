package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HeroServiceIT {

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

        // then
        Optional<Hero> heroOptional = heroService.findById(uuid);
        assertTrue(heroOptional.isPresent());

        Hero createdHero = heroOptional.get();
        assertNotNull(createdHero.getId());
        assertEquals(heroRequest.getName(), createdHero.getName());
        assertEquals(heroRequest.getRace(), createdHero.getRace());
        assertTrue(createdHero.isEnabled());
        assertNotNull(createdHero.getCreatedAt());
        assertNotNull(createdHero.getUpdatedAt());

        PowerStats createdPowerStats = powerStatsService.findById(createdHero.getPowerStatsId());
        assertNotNull(createdPowerStats);
        assertEquals(heroRequest.getAgility(), createdPowerStats.getAgility());
        assertEquals(heroRequest.getStrength(), createdPowerStats.getStrength());
        assertEquals(heroRequest.getDexterity(), createdPowerStats.getDexterity());
        assertEquals(heroRequest.getIntelligence(), createdPowerStats.getIntelligence());
        assertNotNull(createdPowerStats.getCreatedAt());
        assertNotNull(createdPowerStats.getUpdatedAt());
    }

    @Test
    public void createDeveriaLancarNullPointerExceptionSeFaltarNomeNaRequest() {
        // given
        CreateHeroRequest heroRequestWithMissingArgument = createHeroRequestMissingName();

        // when
        NullPointerException e = assertThrows(
                NullPointerException.class, () -> heroService.create(heroRequestWithMissingArgument)
        );

        // then
        assertNotNull(e);
    }

    @Test
    public void findByIdDeveriaRetonarOptionalDeHeroiSeIdExistir() {
        // given
        UUID uuid = heroService.create(createHeroRequest());

        // when
        Optional<Hero> heroOptional = heroService.findById(uuid);

        // then
        assertTrue(heroOptional.isPresent());

        Hero requestedHero = heroOptional.get();
        assertEquals(uuid, requestedHero.getId());
    }

    @Test
    public void findByIdDeveriaRetornarEmptyOptionalSeIdNaoExistir() {
        // given
        UUID uuid = UUID.randomUUID();

        // when
        Optional<Hero> heroOptional = heroService.findById(uuid);

        // then
        assertEquals(heroOptional, Optional.empty());
    }

    @Test
    public void findManyByNameDeveriaRetornarListaCom2HeroisContendoABuscaNoNome() {
        // given
        String search = "man";
        UUID uuid = heroService.create(createSuperManRequest());
        UUID uuid2 = heroService.create(createHeroRequest());

        // when
        List<Hero> heroes = heroService.findManyByName(search);

        // then
        Hero hero = heroService.findById(uuid).get();
        Hero hero2 = heroService.findById(uuid2).get();

        assertEquals(2, heroes.size());
        assertTrue(hero.getName().contains(search));
        assertTrue(hero2.getName().contains(search));
        assertTrue(heroes.contains(hero));
        assertTrue(heroes.contains(hero2));
    }

    @Test
    public void findManyByNameDeveriaRetornarListaVaziaQuandoNenhumHeroiTemABuscaNoNome() {
        // given
        UUID uuidSuperMan = heroService.create(createSuperManRequest());
        UUID uuidBatman = heroService.create(createHeroRequest());

        // when
        List<Hero> heroes = heroService.findManyByName(INVALID_HERO_NAME);

        // then
        Hero hero = heroService.findById(uuidSuperMan).get();
        Hero hero2 = heroService.findById(uuidBatman).get();

        assertEquals(0, heroes.size());
        assertFalse(hero.getName().contains(INVALID_HERO_NAME));
        assertFalse(hero2.getName().contains(INVALID_HERO_NAME));
    }

    @Test
    public void findByNameDeveriaRetornarOptionalDeHeroSeNomeForIgualAPesquisa() {
        // given
        String search = "Batman";
        heroService.create(createHeroRequest());

        // when
        Optional<Hero> heroOptional = heroService.findByName(search);

        // then
        Hero hero = heroOptional.get();
        assertNotNull(hero);
        assertEquals(search, hero.getName());
    }

    @Test
    public void findByNameDeveriaRetornarEmptyOptionalSeNomeForDiferenteDaPesquisa() {
        // given
        UUID uuid = heroService.create(createHeroRequest());
        Hero hero = heroService.findById(uuid).get();

        // when
        Optional<Hero> heroOptional = heroService.findByName(INVALID_HERO_NAME);

        // then
        assertTrue(heroOptional.isEmpty());
        assertNotEquals(INVALID_HERO_NAME, hero.getName());
    }

    @Test
    public void findAll_DeveriaRetornarListaSemNenhumHeroiCasoNaoTenhaHeroisCadastrados() {
        //given
        List<Hero> heroes = heroService.findAll();

        // then
        assertNotNull(heroes);
        assertEquals(0, heroes.size());
    }

    @Test
    public void findAll_DeveriaRetornarListComTodosHerois() {
        //given
        UUID heroId = heroService.create(createHeroRequest());
        UUID heroId2 = heroService.create(createSuperManRequest());

        // when
        List<Hero> heroes = heroService.findAll();

        // then
        assertNotNull(heroes);
        assertEquals(2, heroes.size());
        assertEquals(heroId, heroes.get(0).getId());
        assertEquals(heroId2, heroes.get(1).getId());
    }

    @Test
    public void update_DeveriaAtualizarDadosDoHeroi() {
        // given
        UUID uuid = heroService.create(createHeroRequest());
        Hero hero = heroService.findById(uuid).get();
        UpdateHeroRequest updatedHeroRequest = createUpdatedHeroRequest();

        // when
        heroService.update(hero,updatedHeroRequest);

        // then
        PowerStats powerStats = powerStatsService.findById(hero.getPowerStatsId());
        assertEquals(updatedHeroRequest.getName(), hero.getName());
        assertEquals(updatedHeroRequest.getRace(), hero.getRace());
        assertEquals(updatedHeroRequest.getEnabled(), hero.isEnabled());
        assertEquals(updatedHeroRequest.getAgility(), powerStats.getAgility());
        assertEquals(updatedHeroRequest.getDexterity(), powerStats.getDexterity());
        assertEquals(updatedHeroRequest.getIntelligence(), powerStats.getIntelligence());
        assertEquals(updatedHeroRequest.getStrength(), powerStats.getStrength());
    }

    @Test
    public void deleteDeveriaDeletarHeroiSeHeroExistir() {
        // given
        UUID uuid = heroService.create(createHeroRequest());
        Hero hero = heroService.findById(uuid).get();

        // when
        heroService.delete(hero);

        // then
        Optional<Hero> heroOptional = heroService.findById(uuid);
        assertTrue(heroOptional.isEmpty());

        PowerStats powerStats = powerStatsService.findById(hero.getPowerStatsId());
        assertNull(powerStats);
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


    private CreateHeroRequest createHeroRequestMissingName() {
        return CreateHeroRequest.builder()
                .agility(5)
                .dexterity(8)
                .strength(6)
                .intelligence(10)
                .race(Race.HUMAN)
                .build();
    }

    private UpdateHeroRequest createUpdatedHeroRequest() {
        return UpdateHeroRequest.builder()
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
