package com.best11.player.entity;

import com.best11.competition.entity.Season;
import com.best11.competition.entity.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"player_id","team_id","season_id"}))
public class PlayerSeasonStat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id",nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id",nullable = false)
    private Season season;

    private int goals;
    private int assists;
    private int appearances;

    @Builder
    public PlayerSeasonStat(Player player, Team team, Season season,int goals, int assists, int appearances) {
        this.player = player;
        this.team = team;
        this.season = season;
        this.goals = goals;
        this.assists = assists;
        this.appearances = appearances;
    }
}
