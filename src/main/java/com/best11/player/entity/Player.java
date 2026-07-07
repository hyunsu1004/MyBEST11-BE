package com.best11.player.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String nationality;

    @Column(nullable = false)
    private String primaryPosition; //"ST" , "CB" 등 코드값

    private String photoUrl;

    @Builder
    public Player(String name, String natationality, String primaryPosition, String photoUrl) {
        this.name = name;
        this.nationality = natationality;
        this.primaryPosition = primaryPosition;
        this.photoUrl = photoUrl;
    }
}
