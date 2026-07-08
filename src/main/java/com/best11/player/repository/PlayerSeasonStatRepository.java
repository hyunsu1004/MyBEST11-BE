package com.best11.player.repository;

import com.best11.player.entity.Player;
import com.best11.player.entity.PlayerSeasonStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerSeasonStatRepository extends JpaRepository<PlayerSeasonStat, Long> {
    //특정 팀+시즌 소속선수 목록(FR-5)

    @Query("""
    SELECT pss.player FROM PlayerSeasonStat pss
    WHERE pss.team.id =:teamId AND pss.season.id = :seasonId
""")
    List<Player> findPlayerByTeamAndSeason(
            @Param("teamId")Long teamId,
            @Param("seasonId")Long seasonId
    );

}
