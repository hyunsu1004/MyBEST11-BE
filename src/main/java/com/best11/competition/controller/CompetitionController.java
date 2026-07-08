package com.best11.competition.controller;


import com.best11.common.DTO.ApiResponse;
import com.best11.competition.dto.LeagueResponseDto;
import com.best11.competition.dto.SeasonResponseDto;
import com.best11.competition.dto.TeamResponseDto;
import com.best11.competition.service.CompetitionService;
import com.best11.player.dto.PlayerSummaryResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping("/leagues")
    public ApiResponse<List<LeagueResponseDto>> getLeagues(){
        return ApiResponse.ok(competitionService.getAllLeagues());
    }

    @GetMapping("/leagues/{leagueId}/seasons")
    public ApiResponse<List<SeasonResponseDto>> getSeasons(@PathVariable Long leagueId){
        return ApiResponse.ok(competitionService.getSeasonByLeague(leagueId));
    }

    @GetMapping("/leagues/{leagueId}/seasons/{seasonsId}/teams")
    public ApiResponse<List<TeamResponseDto>> getTeams(
            @PathVariable Long leagueId, @PathVariable Long seasonsId
    ){
        return ApiResponse.ok(competitionService.getTeams(leagueId, seasonsId));
    }

    @GetMapping("/teams/{teamId}/players")
    public ApiResponse<List<PlayerSummaryResponseDto>> getTeamPlayers(
            @PathVariable Long teamId,@RequestParam Long season
    ){
        return ApiResponse.ok(competitionService.getPlayerByTeamAndSeason(teamId, season));
    }
}
