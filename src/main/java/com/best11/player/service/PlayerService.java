package com.best11.player.service;

import com.best11.player.dto.PlayerSummaryResponseDto;
import com.best11.player.entity.Player;
import com.best11.player.repository.PlayerRepository;
import com.best11.player.repository.PlayerSeasonStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerSeasonStatRepository playerSeasonStatRepository;

    public List<PlayerSummaryResponseDto> search(String query,String position){
        List<Player> players = (position != null)
                ? playerRepository.findTop10ByNameContainingIgnoreCaseAndPrimaryPosition(query, position)
                : playerRepository.findTop10ByNameContainingIgnoreCase(query);

        return players.stream().map(this::toSummary).toList();
    }

    public List<PlayerSummaryResponseDto> getPlayersByTeamAndSeason(Long teamId,Long seasonId){
        return playerSeasonStatRepository.findPlayerByTeamAndSeason(teamId,seasonId)
                .stream().map(this::toSummary).toList();
    }

    private PlayerSummaryResponseDto toSummary(Player player){
        return new PlayerSummaryResponseDto(player.getId(),player.getName(),player.getPrimaryPosition(),player.getPhotoUrl());
    }


}
