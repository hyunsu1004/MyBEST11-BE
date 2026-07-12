package com.best11.recommend.entity;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Player")
public class PlayerNode {

    @Id
    private Long id; //MySQL player.id 와 동일한 값 사용

    @Property("name")
    private String name;

    @Property("position")
    private String position;

    public PlayerNode() {}

    public PlayerNode(Long id, String name, String porsition) {
        this.id = id;
        this.name = name;
        this.position = porsition;
    }

    public Long getId(){return id;}
    public String getName(){return name;}
    public String getPosition(){return position;}
}
