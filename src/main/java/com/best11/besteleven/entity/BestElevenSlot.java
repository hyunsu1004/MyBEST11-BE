package com.best11.besteleven.entity;

import com.best11.player.entity.Player;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BestElevenSlot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "best_eleven_id",nullable = false)
    private BestEleven bestEleven;

    @Column(nullable = false)
    private String positionCode; // GK, ST

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id",nullable = false)
    private Player player;

    @Builder
    public BestElevenSlot(String positionCode, Player player) {
        this.positionCode = positionCode;
        this.player = player;
    }

    void assignTo(BestEleven bestEleven) {
        this.bestEleven = bestEleven;
    }
}
