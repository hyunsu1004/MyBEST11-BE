package com.best11.recommend.repository;

import com.best11.recommend.entity.PlayerNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerGraphRepository extends Neo4jRepository<PlayerNode,Long> {

    @Query("""
        MATCH (p:Player {id: $playerId})-[:PLAYS_FOR]->(t:Team)-[:COMPETES_IN]->(l:League)
        MATCH (l)<-[:COMPETES_IN]-(t2:Team)<-[:PLAYS_FOR]-(p2:Player)
        WHERE p2.position = p.position AND p2.id <> p.id
        RETURN DISTINCT p2
        LIMIT 5
    """)
    List<PlayerNode> findSimilarPositionPlayers(@Param("playerId") Long playerId);

    @Query("""
        MATCH (p:Player {id: $playerId})-[:PLAYS_FOR]->(t:Team)<-[:PLAYS_FOR]-(teammate:Player)
        WHERE teammate.id <> p.id
        RETURN DISTINCT teammate
    """)

    List<PlayerNode> findTeammates(@Param("playerId") Long playerId);
}
