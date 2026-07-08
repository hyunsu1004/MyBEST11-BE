package com.best11.competition.repository;

import com.best11.competition.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    @Query("""
        SELECT DISTINCT pss.season FROM PlayerSeasonStat pss
        WHERE pss.team.league.id = :leagueId
        ORDER BY pss.season.yearRange DESC
    """)
    List<Season> findSeasonsByLeagueId(@Param("leagueId") Long leagueId);
}
