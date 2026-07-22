package com.best11.live;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
public class LiveMatchController {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "match";

    /** 현재 Redis에 집계 데이터가 있는 모든 경기 ID 목록**/
    @GetMapping("/matches")
    public List<String> getLiveMatchIds(){
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        if(keys == null){
            return List.of();
        }

        return keys.stream()
                .map(k->k.substring(KEY_PREFIX.length()))
                .sorted()
                .toList();
    }


    /*특정 경기의 실시간 집계 데이터 전체*/
    @GetMapping("/matches/{matchId}")
    public Map<Object,Object> getMatchStats(@PathVariable String matchId){
        return redisTemplate.opsForHash().entries(KEY_PREFIX+ matchId);
    }
}
