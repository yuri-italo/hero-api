package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.powerstats.PowerStatsService;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.dto.HeroDTO;
import br.com.gubee.interview.model.dto.PowerStatsDTO;
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
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HeroController.class)
class HeroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HeroService heroService;

    @MockBean
    private PowerStatsService powerStatsService;

    @BeforeEach
    public void initTest() {
        when(heroService.create(any())).thenReturn(UUID.randomUUID());
    }

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
        UUID uuid = UUID.randomUUID();
        UUID powerStatsUUID = UUID.randomUUID();

        //when
        PowerStatsDTO powerStats = new PowerStatsDTO(9, 5, 5, 9);
        HeroDTO heroDTO = new HeroDTO(uuid, "Batman", Race.HUMAN, Instant.now().toString(), Instant.now().toString(), true, powerStats);
        Hero hero = new Hero(uuid, "Batman", Race.HUMAN, powerStatsUUID, Instant.now(), Instant.now(), true);

        when(heroService.findById(uuid)).thenReturn(Optional.of(hero));

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/" + uuid)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(heroDTO)));
    }

    @Test
    void getById_ReturnCode404_IfUUIDDoNotExists() throws Exception {
        //given
        final UUID heroUUID = UUID.randomUUID();

        //when
        final ResultActions resultActions = mockMvc.perform(get("/api/v1/heroes/" + heroUUID)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNotFound());
        verify(heroService, times(1)).findById(heroUUID);
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