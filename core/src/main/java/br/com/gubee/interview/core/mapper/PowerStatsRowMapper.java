package br.com.gubee.interview.core.mapper;

import br.com.gubee.interview.model.PowerStats;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PowerStatsRowMapper implements RowMapper<PowerStats> {
    @Override
    public PowerStats mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PowerStats.builder()
                .id((UUID) rs.getObject("id"))
                .strength(rs.getInt("strength"))
                .agility(rs.getInt("agility"))
                .dexterity(rs.getInt("dexterity"))
                .intelligence(rs.getInt("intelligence"))
                .createdAt(rs.getTimestamp("created_at").toInstant())
                .updatedAt(rs.getTimestamp("updated_at").toInstant())
                .build();
    }
}
