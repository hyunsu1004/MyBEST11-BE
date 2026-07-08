package com.best11.competition.service;



import com.best11.common.exception.CustomException;
import com.best11.common.exception.ErrorCode;
import com.best11.competition.dto.LeagueResponseDto;
import com.best11.competition.dto.SeasonResponseDto;
import com.best11.competition.dto.TeamResponseDto;
import com.best11.competition.entity.League;
import com.best11.competition.entity.Season;
import com.best11.competition.entity.Team;
import com.best11.competition.repository.LeagueRepository;
import com.best11.competition.repository.SeasonRepository;
import com.best11.competition.repository.TeamRepository;
import com.best11.player.dto.PlayerSummaryResponseDto;
import com.best11.player.entity.Player;
import com.best11.player.repository.PlayerSeasonStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompetitionService {

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final SeasonRepository seasonRepository;
    private final PlayerSeasonStatRepository playerSeasonStatRepository;

    public List<LeagueResponseDto> getAllLeagues() {
        return leagueRepository.findAll().stream()
                .map(this::toLeagueResponse)
                .toList();
    }

    //리그를 먼저 선택하는 경우 ------
    public List<SeasonResponseDto> getAllSeasons() {
        // 시즌은 리그에 종속되지 않은 전역 값 (2024-25 등) 이라, 리그 무관하게 전체 반환
        return seasonRepository.findAll().stream()
                .map(this::toSeasonResponse)
                .toList();
    }

    //시즌을 먼저 선택하는 경우 -------(선택)
    public List<SeasonResponseDto> getSeasonByLeague(Long leagueId) {
        if(!leagueRepository.existsById(leagueId)) {
            throw new CustomException(ErrorCode.LEAGUE_NOT_FOUND);
        }
        return seasonRepository.findSeasonsByLeagueId(leagueId).stream()
                .map(this::toSeasonResponse)
                .toList();
    }

    public List<TeamResponseDto> getTeams(Long leagueId, Long seasonId) {
        if(!leagueRepository.existsById(leagueId)) {
            throw new CustomException(ErrorCode.LEAGUE_NOT_FOUND);
        }
        return teamRepository.findTeamByLeagueIdAndSeason(leagueId,seasonId).stream()
                .map(this::toTeamResponse)
                .toList();
    }

    public List<PlayerSummaryResponseDto> getPlayerByTeamAndSeason(Long teamId, Long seasonId) {
        return playerSeasonStatRepository.findPlayerByTeamAndSeason(teamId,seasonId).stream()
                .map(this::toPlayerSummary)
                .toList();
    }

    private LeagueResponseDto toLeagueResponse(League league) {
        return new LeagueResponseDto(league.getId(), league.getName(), league.getCountry());
    }

    private SeasonResponseDto toSeasonResponse(Season season) {
        return new SeasonResponseDto(season.getId(), season.getYearRange());
    }

    private TeamResponseDto toTeamResponse(Team team) {
        return new TeamResponseDto(team.getId(), team.getName());
    }

    private PlayerSummaryResponseDto toPlayerSummary(Player player) {
        return new PlayerSummaryResponseDto(
                player.getId(), player.getName(), player.getPrimaryPosition(), player.getPhotoUrl()
        );
    }



}
