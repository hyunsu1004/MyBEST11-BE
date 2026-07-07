package com.best11.formation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Formation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name; // " 4- 3- 3"

    @Column(columnDefinition = "json",nullable = false)
    private String positionLayout; //JSON문자열로 저장 서비스 레이어에서 파싱

    @Builder
    public Formation(String name, String positionLayout) {
        this.name = name;
        this.positionLayout = positionLayout;
    }
}

//TODO :