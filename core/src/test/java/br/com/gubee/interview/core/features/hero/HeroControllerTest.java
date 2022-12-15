package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.dto.UpdatedHeroDTO;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HeroController.class)
class HeroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PowerStatsService powerStatsService;

    @MockBean
    private HeroService heroService;

    @Test
    void create_DeveriaRetornarCodigo200EHeaderComLocationDoHeroCriado() throws Exception {
        //given
        // Convert the hero request into a string JSON format stub.
        final String body = objectMapper.writeValueAsString(createHeroRequest());

        //when
        final ResultActions resultActions = mockMvc.perform(post("/api/v1/heroes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body));

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void createDeveriaRetornarCodigo400SeFaltarPropriedadeNaRequesicao() throws Exception {
        //given
        // Convert the hero request into a string JSON format stub.
        final String body = objectMapper.writeValueAsString(createHeroRequestMissingArgument());

        //when
        final ResultActions resultActions = mockMvc.perform(post("/api/v1/heroes")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void getById_DeveriaRetornarCode200EDadosCorretosDoHeroiNaRepostaSeIdExistir() throws Exception {
        //given
        UUID heroId = UUID.randomUUID();
        UUID powerStatsId = UUID.randomUUID();
        Hero hero = getBatman(heroId, powerStatsId);
        PowerStats powerStats = getBatmanPowerStats(powerStatsId);

        when(heroService.findById(heroId)).thenReturn(Optional.of(hero));
        when(powerStatsService.findById(hero.getPowerStatsId())).thenReturn(powerStats);

        // when
        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/{heroId}",heroId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(hero.getId().toString()))
                .andExpect(jsonPath("$.race").value(hero.getRace().toString()))
                .andExpect(jsonPath("$.enabled").value(hero.isEnabled()))
                .andExpect(jsonPath("$.created_at").value(hero.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updated_at").value(hero.getUpdatedAt().toString()))
                .andExpect(jsonPath("$.power_stats.strength").value(powerStats.getStrength()))
                .andExpect(jsonPath("$.power_stats.agility").value(powerStats.getAgility()))
                .andExpect(jsonPath("$.power_stats.dexterity").value(powerStats.getDexterity()))
                .andExpect(jsonPath("$.power_stats.intelligence").value(powerStats.getIntelligence()));
    }

    @Test
    void getByIdDeveriaRetornarCodigo404SeIdNaoExistir() throws Exception {
        //given
        final UUID heroId = UUID.randomUUID();

        when(heroService.findById(heroId)).thenReturn(Optional.empty());

        //when
        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/{heroId}", heroId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void findManyByName_DeveriaRetonarCodigo200EListaComDoisHeroisContendoAPesquisaNoNome() throws Exception {
        //given
        String search = "man";

        UUID batmanId = UUID.randomUUID();
        UUID powerStatsBatmanId = UUID.randomUUID();
        Hero batman = getBatman(batmanId,powerStatsBatmanId);
        PowerStats batmanPowerStats = getBatmanPowerStats(powerStatsBatmanId);

        UUID powerStatsSupermanId = UUID.randomUUID();
        UUID supermanId = UUID.randomUUID();
        Hero superman = getSuperman(supermanId, powerStatsSupermanId);
        PowerStats supermanPowerStats = getBatmanPowerStats(powerStatsSupermanId);

        List<Hero> heroes = Arrays.asList(batman,superman);


        // when
        when(heroService.findManyByName(search)).thenReturn(heroes);
        when(powerStatsService.findById(batman.getPowerStatsId())).thenReturn(batmanPowerStats);
        when(powerStatsService.findById(superman.getPowerStatsId())).thenReturn(supermanPowerStats);


        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/search/{heroName}",search)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        assertEquals(2, heroes.size());

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].name", containsString(search)))
                .andExpect(jsonPath("$.[1].name", containsString(search)));
    }

    @Test
    void findManyByNameDeveriaRetonarCodigo200EListaVaziaSeNaoConterHeroisComAPesquisaNoNome() throws Exception {
        //given
        String search = "man";
        List<Hero> heroes = new ArrayList<>();

        // when
        when(heroService.findManyByName(search)).thenReturn(heroes);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/search/{heroName}",search)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        assertEquals(0,heroes.size());

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void listDeveriaRetornarListaCom1HeroiExistente() throws Exception {
        //given
        UUID heroId = UUID.randomUUID();
        UUID powerStatsId = UUID.randomUUID();
        Hero hero = getSuperman(heroId,powerStatsId);
        PowerStats powerStats = getSupermanPowerStats(powerStatsId);
        List<Hero> heroes = List.of(hero);

        // when
        when(heroService.findAll()).thenReturn(heroes);
        when(powerStatsService.findById(powerStatsId)).thenReturn(powerStats);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        assertEquals(1, heroes.size());

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(heroId.toString()));
    }

    @Test
    void compareDeveriaRetornarCodigo200EListaComDoisHeroisComparadosAoBuscarNomesExistentes() throws Exception {
        //given
        String name1 = "Batman";
        String name2 = "Superman";

        UUID batmanId = UUID.randomUUID();
        UUID powerStatsBatmanId = UUID.randomUUID();
        Hero batman = getBatman(batmanId,powerStatsBatmanId);
        PowerStats batmanPowerStats = getBatmanPowerStats(powerStatsBatmanId);

        UUID powerStatsSupermanId = UUID.randomUUID();
        UUID supermanId = UUID.randomUUID();
        Hero superman = getSuperman(supermanId, powerStatsSupermanId);
        PowerStats supermanPowerStats = getBatmanPowerStats(powerStatsSupermanId);

        // when
        when(heroService.findByName(name1)).thenReturn(Optional.of(batman));
        when(heroService.findByName(name2)).thenReturn(Optional.of(superman));
        when(powerStatsService.findById(batman.getPowerStatsId())).thenReturn(batmanPowerStats);
        when(powerStatsService.findById(superman.getPowerStatsId())).thenReturn(supermanPowerStats);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/compare?hero1Name=" + name1 + "&hero2Name=" + name2)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(batman.getId().toString()))
                .andExpect(jsonPath("$.[0].name").value(batman.getName()))
                .andExpect(jsonPath("$.[0].strength").value(batmanPowerStats.getStrength() - supermanPowerStats.getStrength()))
                .andExpect(jsonPath("$.[0].agility").value(batmanPowerStats.getAgility() - supermanPowerStats.getAgility()))
                .andExpect(jsonPath("$.[0].dexterity").value(batmanPowerStats.getDexterity() - supermanPowerStats.getDexterity()))
                .andExpect(jsonPath("$.[0].intelligence").value(batmanPowerStats.getIntelligence() - supermanPowerStats.getIntelligence()))
                .andExpect(jsonPath("$.[1].id").value(superman.getId().toString()))
                .andExpect(jsonPath("$.[1].name").value(superman.getName()))
                .andExpect(jsonPath("$.[1].strength").value(supermanPowerStats.getStrength() - batmanPowerStats.getStrength()))
                .andExpect(jsonPath("$.[1].agility").value(supermanPowerStats.getAgility() - batmanPowerStats.getAgility()))
                .andExpect(jsonPath("$.[1].dexterity").value(supermanPowerStats.getDexterity() - batmanPowerStats.getDexterity()))
                .andExpect(jsonPath("$.[1].intelligence").value(supermanPowerStats.getIntelligence() - batmanPowerStats.getIntelligence()));
    }

    @Test
    void compare_DeveriaRetornarCodigo404SeNomeDeHeroiNaoExistir() throws Exception {
        //given
        String name1 = "batman";
        String name2 = "spider";

        // when
        when(heroService.findByName(name1)).thenReturn(Optional.empty());
        when(heroService.findByName(name2)).thenReturn(any());

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/compare?hero1Name=" + name1 + "&hero2Name=" + name2)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void updateDeveriaRetornarCodigo200EDadosDeHeroiAtulizadoCasoIdExista() throws Exception {
        //given
        UUID heroId = UUID.randomUUID();
        UUID powerStatsId = UUID.randomUUID();
        Hero hero = getBatman(heroId,powerStatsId);
        PowerStats powerStats = getBatmanPowerStats(powerStatsId);

        UpdatedHeroDTO updatedHeroDTO = createUpdatedHeroDTO();
        final String body = objectMapper.writeValueAsString(updatedHeroDTO);


        // when
        when(heroService.findById(heroId)).thenReturn(Optional.of(hero));
        Hero updatedHerto = heroService.update(hero, updatedHeroDTO);
        when(powerStatsService.findById(hero.getPowerStatsId())).thenReturn(powerStats);

        final ResultActions resultActions = mockMvc.perform(patch("/api/v1/heroes/{heroId}", heroId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(hero.getId().toString()))
                .andExpect(jsonPath("$.name").value(hero.getName()))
                .andExpect(jsonPath("$.race").value("$.name"))
                .andExpect(jsonPath("$.created_at").value(hero.getCreatedAt()))
                .andExpect(jsonPath("$.updated_at").value(hero.getUpdatedAt()))
                .andExpect(jsonPath("$.enabled").value(hero.isEnabled()))
                .andExpect(jsonPath("$.power_stats.strength").value(powerStats.getStrength()))
                .andExpect(jsonPath("$.power_stats.agility").value(powerStats.getAgility()))
                .andExpect(jsonPath("$.power_stats.dexterity").value(powerStats.getDexterity()))
                .andExpect(jsonPath("$.power_stats.intelligence").value(powerStats.getIntelligence()));
    }
//
//    @Test
//    void update_ReturnCode404_IfHeroIdNotExists() throws Exception {
//        //given
//        UUID heroId = UUID.randomUUID();
//
//        // when
//        when(heroService.findById(heroId)).thenReturn(Optional.empty());
//
//        final ResultActions resultActions = mockMvc.perform(patch("/api/v1/heroes/{heroId}", heroId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(updatedHeroDTO)));
//
//        //then
//        resultActions.andExpect(status().isNotFound());
//    }
//
//    @Test
//    void delete_ReturnCode204_IfHeroIdExists() throws Exception {
//        //given
//        UUID heroId = UUID.randomUUID();
//
//        // when
//        when(heroService.findById(heroId)).thenReturn(Optional.of(hero));
//
//        final ResultActions resultActions = mockMvc.perform(delete("/api/v1/heroes/{heroId}", heroId)
//                .contentType(MediaType.APPLICATION_JSON));
//
//        //then
//        resultActions.andExpect(status().isNoContent());
//    }
//
//    @Test
//    void delete_ReturnCode404_IfHeroIdNotExists() throws Exception {
//        //given
//        UUID heroId = UUID.randomUUID();
//
//        // when
//        when(heroService.findById(heroId)).thenReturn(Optional.empty());
//
//        final ResultActions resultActions = mockMvc.perform(delete("/api/v1/heroes/{heroId}", heroId)
//                .contentType(MediaType.APPLICATION_JSON));
//
//        //then
//        resultActions.andExpect(status().isNotFound());
//    }

    private Hero getBatman(UUID heroId, UUID powerStatsId) {
        return new Hero(heroId,"Batman",Race.HUMAN, powerStatsId, Instant.now(),Instant.now(),true);
    }

    private Hero getSuperman(UUID heroId, UUID powerStatsId) {
        return new Hero(heroId,"Superman",Race.HUMAN, powerStatsId, Instant.now(),Instant.now(),true);
    }

    private PowerStats getBatmanPowerStats(UUID powerStatsId) {
        return new PowerStats(powerStatsId,9,9,9,9,Instant.now(),Instant.now());
    }

    private PowerStats getSupermanPowerStats(UUID powerStatsId) {
        return new PowerStats(powerStatsId,10,10,10,10,Instant.now(),Instant.now());
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

    private CreateHeroRequest createHeroRequestMissingArgument() {
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
}