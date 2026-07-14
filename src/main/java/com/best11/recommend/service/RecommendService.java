package com.best11.recommend.service;

import com.best11.recommend.dto.RecommendedPlayerResponseDto;
import com.best11.recommend.entity.PlayerNode;
import com.best11.recommend.repository.PlayerGraphRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final PlayerGraphRepository playerGraphRepository;

    @Transactional("transactionManager")
    public List<RecommendedPlayerResponseDto> recommendSimilarPosition(Long playerId){
        return playerGraphRepository.findSimilarPositionPlayers(playerId).stream()
                .map(this::toResponse)
                .toList();

    }

    @Transactional("transactionManager")
    public List<RecommendedPlayerResponseDto> recommendTeammates(Long playerId){
        return playerGraphRepository.findTeammates(playerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private RecommendedPlayerResponseDto toResponse(PlayerNode node) {
        return new RecommendedPlayerResponseDto(node.getId(),node.getName(),node.getPosition());

    }
}
