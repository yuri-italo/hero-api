package br.com.gubee.interview.core.mapper;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.enums.Race;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class HeroRowMapper implements RowMapper<Hero> {

    @Override
    public Hero mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Hero.builder()
                .id((UUID) rs.getObject("id"))
                .name(rs.getString("name"))
                .race(Race.valueOf(rs.getString("race")))
                .powerStatsId((UUID) rs.getObject("power_stats_id"))
                .enabled(rs.getBoolean("enabled"))
                .createdAt(rs.getTimestamp("created_at").toInstant())
                .updatedAt(rs.getTimestamp("updated_at").toInstant())
                .build();
    }
}
