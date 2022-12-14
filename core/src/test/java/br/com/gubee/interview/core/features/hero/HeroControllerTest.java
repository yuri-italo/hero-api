package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.dto.HeroDTO;
import br.com.gubee.interview.model.dto.ResumedHeroDTO;
import br.com.gubee.interview.model.dto.UpdatedHeroDTO;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private UUID heroId;
    private UUID powerStatsId;
    private UUID powerStatsId2;
    private Hero hero;
    private Hero hero2;
    private PowerStats powerStats;
    private PowerStats powerStats2;
    private UpdatedHeroDTO updatedHeroDTO;
    private HeroDTO heroDTO;

    @Test
    void create_ReturnCode201AndLocationHeader_IfAllRequiredArguments() throws Exception {
        //given
        // Convert the hero request into a string JSON format stub.
        final String body = objectMapper.writeValueAsString(createHeroRequest());

        //when
        final ResultActions resultActions = mockMvc.perform(post("/api/v1/heroes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body));

        //then
        resultActions.andExpect(status().isCreated()).andExpect(header().exists("Location"));
        verify(heroService, times(1)).create(any());
    }

    @Test
    void create_ReturnCode400_IfMissingRequiredArguments() throws Exception {
        //given
        // Convert the hero request into a string JSON format stub.
        final String body = objectMapper.writeValueAsString(createHeroRequestMissingArgument());

        //when
        final ResultActions resultActions = mockMvc.perform(post("/api/v1/heroes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        //then
        resultActions.andExpect(status().isBadRequest());
        verify(heroService, never()).create(any());
    }

    @Test
    void getById_ReturnCode200_IfUUIDExists() throws Exception {
        //given
        String expectedJson = objectMapper.writeValueAsString(new HeroDTO(hero, powerStats));

        // when
        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/{heroId}",heroId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

        verify(heroService, times(1)).findById(heroId);
        verify(powerStatsService,times(1)).findById(powerStatsId);
    }

    @Test
    void getById_ReturnCode404_IfUUIDDoNotExists() throws Exception {
        //given
        final UUID heroId = UUID.randomUUID();

        //when
        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/{heroId}", heroId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNotFound());
        verify(heroService, times(1)).findById(heroId);
    }

    @Test
    void findManyByName_ReturnCode200_IfNameExists() throws Exception {
        //given
        String name = "batman";
        List<Hero> heroes = List.of(hero);
        List<PowerStats> powerStatsList = List.of(powerStats);
        List<HeroDTO> heroDTOList = HeroDTO.toCollectionDTO(heroes, powerStatsList);
        String expectedJson = objectMapper.writeValueAsString(heroDTOList);

        // when
        when(heroService.findManyByName(name)).thenReturn(heroes);
        when(powerStatsService.findById(powerStatsId)).thenReturn(powerStats);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/search/{heroName}",name)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

        verify(heroService, times(1)).findManyByName(name);
        verify(powerStatsService,times(1)).findById(powerStatsId);
    }

    @Test
    void findManyByName_ReturnCode200AndEmptyList_IfNameNotExists() throws Exception {
        //given
        String name = "batman";
        String expectedJson = objectMapper.writeValueAsString(new ArrayList<>());

        // when
        when(heroService.findManyByName(name)).thenReturn(new ArrayList<>());
        when(powerStatsService.findById(powerStatsId)).thenReturn(null);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/search/{heroName}",name)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

        verify(heroService, times(1)).findManyByName(name);
        verify(powerStatsService,never()).findById(powerStatsId);
    }

    @Test
    void list_ReturnCode200_IfHeroesExistsOrNot() throws Exception {
        //given
        List<Hero> heroes = List.of(hero);
        List<PowerStats> powerStatsList = List.of(powerStats);
        List<ResumedHeroDTO> heroDTOList = ResumedHeroDTO.toCollectionDTO(heroes, powerStatsList);
        String expectedJson = objectMapper.writeValueAsString(heroDTOList);

        // when
        when(heroService.findAll()).thenReturn(heroes);
        when(powerStatsService.findById(powerStatsId)).thenReturn(powerStats);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

        verify(heroService, times(1)).findAll();
        verify(powerStatsService,times(1)).findById(powerStatsId);
    }

    @Test
    void compare_ReturnCode200_IfHeroesNamesExists() throws Exception {
        //given
        String name1 = "batman";
        String name2 = "spider";

        // when
        when(heroService.findByName(name1)).thenReturn(Optional.of(hero));
        when(heroService.findByName(name2)).thenReturn(Optional.of(hero2));
        when(powerStatsService.findById(powerStatsId)).thenReturn(powerStats);
        when(powerStatsService.findById(powerStatsId2)).thenReturn(powerStats2);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/compare?hero1Name=" + name1 + "&hero2Name=" + name2)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk());

        verify(heroService, times(2)).findByName(any());
        verify(powerStatsService,times(2)).findById(any());
    }

    @Test
    void compare_ReturnCode404_IfHeroNameNotExists() throws Exception {
        //given
        String name1 = "batman";
        String name2 = "spider";

        // when
        when(heroService.findByName(name1)).thenReturn(Optional.empty());
        when(heroService.findByName(name2)).thenReturn(Optional.empty());

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/compare?hero1Name=" + name1 + "&hero2Name=" + name2)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNotFound());

        verify(heroService, times(2)).findByName(any());
        verify(powerStatsService,never()).findById(any());
    }

    @Test
    void update_ReturnCode200_IfHeroDataChanges() throws Exception {
        //given
        String expectedJson = objectMapper.writeValueAsString(heroDTO);

        // when
        when(heroService.findById(heroId)).thenReturn(Optional.of(hero));
        when(powerStatsService.findById(hero.getPowerStatsId())).thenReturn(powerStats);

        final ResultActions resultActions = mockMvc.perform(patch("/api/v1/heroes/{heroId}", heroId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedHeroDTO)));

        //then
        resultActions.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.content().json(expectedJson));;

        verify(heroService, times(1)).findById(heroId);
        verify(powerStatsService, times(1)).findById(powerStatsId);
    }

    @Test
    void update_ReturnCode404_IfHeroIdNotExists() throws Exception {
        //given
        UUID heroId = UUID.randomUUID();

        // when
        when(heroService.findById(heroId)).thenReturn(Optional.empty());

        final ResultActions resultActions = mockMvc.perform(patch("/api/v1/heroes/{heroId}", heroId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedHeroDTO)));

        //then
        resultActions.andExpect(status().isNotFound());

        verify(heroService, times(1)).findById(heroId);
        verify(powerStatsService, never()).findById(any());
    }

    @Test
    void delete_ReturnCode204_IfHeroIdExists() throws Exception {
        //given
        UUID heroId = UUID.randomUUID();

        // when
        when(heroService.findById(heroId)).thenReturn(Optional.of(hero));

        final ResultActions resultActions = mockMvc.perform(delete("/api/v1/heroes/{heroId}", heroId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNoContent());

        verify(heroService, times(1)).findById(heroId);
        verify(heroService, times(1)).delete(hero);
    }

    @Test
    void delete_ReturnCode404_IfHeroIdNotExists() throws Exception {
        //given
        UUID heroId = UUID.randomUUID();

        // when
        when(heroService.findById(heroId)).thenReturn(Optional.empty());

        final ResultActions resultActions = mockMvc.perform(delete("/api/v1/heroes/{heroId}", heroId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNotFound());

        verify(heroService, times(1)).findById(heroId);
        verify(heroService, never()).delete(any());
    }

    @BeforeEach
    public void initTest() {
        heroId = UUID.randomUUID();
        UUID heroId2 = UUID.randomUUID();
        powerStatsId = UUID.randomUUID();
        powerStatsId2 = UUID.randomUUID();
        UUID updatedHeroId = UUID.randomUUID();
        hero = new Hero(heroId,"Batman",Race.HUMAN,powerStatsId, Instant.now(),Instant.now(),true);
        hero2 = new Hero(heroId2,"Spider",Race.ALIEN,powerStatsId2,Instant.now(),Instant.now(),true);
        powerStats = new PowerStats(powerStatsId,9,9,9,9,Instant.now(),Instant.now());
        powerStats2 = new PowerStats(powerStatsId,8,8,8,8,Instant.now(),Instant.now());
        updatedHeroDTO = new UpdatedHeroDTO(updatedHeroId.toString(),"Spider",Race.ALIEN,5,6,7,8,false);
        heroDTO = new HeroDTO(hero,powerStats);
        when(heroService.create(any())).thenReturn(UUID.randomUUID());
        when(heroService.findById(heroId)).thenReturn(Optional.of(hero));
        when(powerStatsService.findById(powerStatsId)).thenReturn(powerStats);
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
}