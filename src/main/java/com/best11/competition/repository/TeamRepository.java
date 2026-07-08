package com.best11.competition.repository;

import com.best11.competition.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByLeagueId(Long leagueId);
    //Team- season 연관을 어떻게 모델링했는지에 따라 실제 쿼리 메서드명은 달라질 수 있음
    @Query("""
            SELECT DISTINCT t FROM Team t 
            JOIN PlayerSeasonStat pss ON pss.team = t 
            WHERE t.league.id = :leagueId AND pss.season.id = :seasonId""")

    List<Team> findTeamByLeagueIdAndSeason(@Param("leagueId")Long leagueId
            , @Param("seasonId")Long seasonId);
    }
