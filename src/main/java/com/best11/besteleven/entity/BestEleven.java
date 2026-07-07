package com.best11.besteleven.entity;

import com.best11.common.entity.BaseTimeEntity;
import com.best11.formation.entity.Formation;
import com.best11.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BestEleven extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id",nullable = false)
    private Formation formation;

    @Column(nullable = false)
    private String titie;

    @OneToMany(mappedBy = "bestEleven",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BestElevenSlot> slots =  new ArrayList<>();

    @Builder
    public BestEleven(User user, Formation formation, String titie) {
        this.user = user;
        this.formation = formation;
        this.titie = titie;
    }


    /*addSlot()처럼 연관관계 편의 메서드를 엔티티 안에 두면,
    서비스 레이어에서 양쪽을 따로 set 하다가 빠뜨리는 실수를 막을 수 있습니다.
    cascade = ALL, orphanRemoval = true는 "BestEleven이 삭제되면 슬롯도 같이 삭제, 슬롯 리스트에서 빠지면 그 슬롯도 삭제"를 의미합니다.*/

    public void addSlot(BestElevenSlot slot) {
        slots.add(slot);
        slot.assignTo(this);
    }

    public void updateSlot(String titie) {
        this.titie = titie;
    }
}
