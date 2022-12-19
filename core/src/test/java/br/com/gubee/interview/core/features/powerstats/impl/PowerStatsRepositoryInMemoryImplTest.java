package br.com.gubee.interview.core.features.powerstats.impl;

import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PowerStatsRepositoryInMemoryImplTest {

    private PowerStatsRepositoryInMemoryImpl powerStatsRepositoryInMemory;

    @BeforeEach
    void setUp() {
        powerStatsRepositoryInMemory = new PowerStatsRepositoryInMemoryImpl();
    }

    @Test
    void createShouldCreatePowerStatsIfAllArgumentsArePresent() {
        // given
        PowerStats powerStats = createPowerStats();

        // when
        UUID uuid = powerStatsRepositoryInMemory.create(powerStats);

        // then
        assertNotNull(uuid);

        PowerStats createdPowerStats = PowerStatsRepositoryInMemoryImpl.powerStatsStorage.get(uuid);
        assertNotNull(createdPowerStats);
        assertEquals(powerStats.getId(), createdPowerStats.getId());
        assertEquals(powerStats.getAgility(), createdPowerStats.getAgility());
        assertEquals(powerStats.getDexterity(), createdPowerStats.getDexterity());
        assertEquals(powerStats.getIntelligence(), createdPowerStats.getIntelligence());
        assertEquals(powerStats.getStrength(), createdPowerStats.getStrength());
        assertEquals(powerStats.getCreatedAt(), createdPowerStats.getCreatedAt());
        assertEquals(powerStats.getUpdatedAt(), createdPowerStats.getUpdatedAt());
    }

    @Test
    void createShouldThrowIllegalArgumentExceptionIfPowerStatsParameterHaveId() {
        // given
        PowerStats powerStats = createPowerStatsWithId();

        // when
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class, () -> powerStatsRepositoryInMemory.create(powerStats)
        );

        // then
        assertNotNull(e);
    }

    @Test
    void findByIdShouldReturnPowerStatsIfIdExists() {
        // given
        PowerStats powerStats = createPowerStats();
        UUID uuid = powerStatsRepositoryInMemory.create(powerStats);

        // when
        PowerStats powerStatsById = powerStatsRepositoryInMemory.findById(uuid);

        // then
        assertNotNull(powerStatsById);
        assertEquals(powerStats.getId(), powerStatsById.getId());
        assertEquals(powerStats.getAgility(), powerStatsById.getAgility());
        assertEquals(powerStats.getDexterity(), powerStatsById.getDexterity());
        assertEquals(powerStats.getIntelligence(), powerStatsById.getIntelligence());
        assertEquals(powerStats.getStrength(), powerStatsById.getStrength());
        assertEquals(powerStats.getCreatedAt(), powerStatsById.getCreatedAt());
        assertEquals(powerStats.getUpdatedAt(), powerStatsById.getUpdatedAt());
    }

    @Test
    void findByIdShouldNotReturnPowerStatsIfIdNotExists() {
        // given
        UUID uuid = UUID.randomUUID();

        // when
        PowerStats powerStatsById = powerStatsRepositoryInMemory.findById(uuid);

        // then
        assertNull(powerStatsById);
    }

    @Test
    void updateShouldUpdatePowerStatsFieldsAccordingToRequestIfIdExists() {
        // given
        UUID uuid = powerStatsRepositoryInMemory.create(createPowerStats());
        PowerStats powerStats = powerStatsRepositoryInMemory.findById(uuid);
        Instant updateTimeBeforeUpdate = powerStats.getUpdatedAt();
        Instant createTimeBeforeUpdate = powerStats.getCreatedAt();
        UpdateHeroRequest request = createUpdateHeroRequest();

        // when
        powerStatsRepositoryInMemory.update(powerStats,request);

        // then
        assertEquals(powerStats.getAgility(), request.getAgility());
        assertEquals(powerStats.getDexterity(), request.getDexterity());
        assertEquals(powerStats.getIntelligence(), request.getIntelligence());
        assertEquals(powerStats.getStrength(), request.getStrength());
        assertNotEquals(updateTimeBeforeUpdate,powerStats.getUpdatedAt());
        assertEquals(createTimeBeforeUpdate,powerStats.getCreatedAt());
    }

    @Test
    void updateShouldThrowNullPointerExceptionWhenPowerStatsIsNull() {
        // given
        PowerStats powerStats = null;
        UpdateHeroRequest request = createUpdateHeroRequest();

        // when
        NullPointerException e = assertThrows(
                NullPointerException.class, () -> powerStatsRepositoryInMemory.update(powerStats, request)
        );

        // then
        assertNotNull(e);
    }

    @Test
    void updateShouldThrowNullPointerExceptionWhenRequestIsNull() {
        // given
        UUID uuid = powerStatsRepositoryInMemory.create(createPowerStats());
        PowerStats powerStats = PowerStatsRepositoryInMemoryImpl.powerStatsStorage.get(uuid);
        UpdateHeroRequest request = null;

        // when
        NullPointerException e = assertThrows(
                NullPointerException.class, () -> powerStatsRepositoryInMemory.update(powerStats, request)
        );

        // then
        assertNotNull(e);
    }

    @Test
    void deleteShouldDeletePowerStatsIfIdExists() {
        // given
        UUID uuid = powerStatsRepositoryInMemory.create(createPowerStats());

        // when
        powerStatsRepositoryInMemory.delete(uuid);

        // then
        PowerStats powerStats = PowerStatsRepositoryInMemoryImpl.powerStatsStorage.get(uuid);
        assertNull(powerStats);
    }

    @Test
    void deleteShouldReturnNullPointerExceptionIfIdNotExists() {
        // given
        UUID uuid = UUID.randomUUID();

        // when
        NullPointerException e = assertThrows(
                NullPointerException.class, () -> powerStatsRepositoryInMemory.delete(uuid)
        );

        // then
        assertNotNull(e);
    }

    private PowerStats createPowerStats() {
        return PowerStats.builder()
                .agility(10)
                .dexterity(10)
                .intelligence(10)
                .strength(10)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private PowerStats createPowerStatsWithId() {
        return PowerStats.builder()
                .id(UUID.randomUUID())
                .agility(10)
                .dexterity(10)
                .intelligence(10)
                .strength(10)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private UpdateHeroRequest createUpdateHeroRequest() {
        return UpdateHeroRequest.builder()
                .agility(9)
                .dexterity(9)
                .intelligence(9)
                .strength(9)
                .build();
    }
}