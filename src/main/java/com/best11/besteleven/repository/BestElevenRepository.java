package com.best11.besteleven.repository;

import com.best11.besteleven.entity.BestEleven;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface BestElevenRepository extends JpaRepository<BestEleven, Long> {

    List<BestEleven> findByUserIdOrderByCreatedAtDesc(Long userId);

    //소유권 검증까지 쿼리 레벨에서 - Service에서 "남의 글 수정 방지"에 사용
    Optional<BestEleven> findByIdAndUserId(Long id, Long userId);

    //슬롯까지 한번에 로딩
    @Query("""
        SELECT DISTINCT be FROM BestEleven be
        LEFT JOIN FETCH be.slots s
        LEFT JOIN FETCH s.player
        WHERE be.id = :id
    """)
    Optional<BestEleven> findByIdWithSlots(@Param("id") Long id);
}
