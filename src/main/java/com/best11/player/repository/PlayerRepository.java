package com.best11.player.repository;

import com.best11.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    //자동완성 검색(FR-3)
    List<Player> findTop10ByNameContainingIgnoreCase(String query);

    List<Player> findTop10ByNameContainingIgnoreCaseAndPrimaryPosition(String query, String positionCode);
}
