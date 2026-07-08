package com.best11.besteleven.service;


import com.best11.besteleven.dto.request.BestElevenCreateRequestDto;
import com.best11.besteleven.dto.request.BestElevenUpdateRequestDto;
import com.best11.besteleven.dto.request.SlotRequestDto;
import com.best11.besteleven.dto.response.BestElevenResponseDto;
import com.best11.besteleven.dto.response.SlotResponseDto;
import com.best11.besteleven.entity.BestEleven;
import com.best11.besteleven.entity.BestElevenSlot;
import com.best11.besteleven.repository.BestElevenRepository;
import com.best11.common.exception.CustomException;
import com.best11.formation.entity.Formation;
import com.best11.formation.repository.FormationRepository;
import com.best11.player.dto.PlayerSummaryResponseDto;
import com.best11.player.entity.Player;
import com.best11.player.repository.PlayerRepository;
import com.best11.user.entity.User;
import com.best11.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BestElevenService {
    private final BestElevenRepository bestElevenRepository;
    private final UserRepository userRepository;
    private final FormationRepository formationRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public BestElevenResponseDto create(Long userId, BestElevenCreateRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("존재하지 않는 유저입니다."));

        Formation formation = formationRepository.findById(requestDto.formationId()).orElseThrow(() -> new CustomException("존재하지 않는 포메이션입니다."));

        validateSlotsMatchFormation(requestDto.slots(), formation);

        BestEleven bestEleven = BestEleven.builder()
                .user(user)
                .formation(formation)
                .titie(requestDto.title())
                .build();

        for (SlotRequestDto slotRequest : requestDto.slots()) {
            Player player = playerRepository.findById(slotRequest.playerId())
                    .orElseThrow(() -> new CustomException("존재하지 않는 선수입니다: " + slotRequest.playerId()));

            bestEleven.addSlot(BestElevenSlot.builder()
                    .positionCode(slotRequest.positionCode())
                    .player(player)
                    .build());
        }

        BestEleven saved = bestElevenRepository.save(bestEleven);
        return toResponse(saved);
    }
    @Transactional
    public BestElevenResponseDto update(Long userId, Long bestElevenId, BestElevenUpdateRequestDto requestDto) {
        BestEleven bestEleven = bestElevenRepository.findByIdAndUserId(bestElevenId, userId)
                .orElseThrow(() -> new CustomException("본인 소유의 베스트11만 수정할 수 있습니다."));

        validateSlotsMatchFormation(requestDto.slots(), bestEleven.getFormation());

        bestEleven.updateSlot(requestDto.title());
        bestEleven.replaceSlots(buildSlots(requestDto.slots()));

        return toResponse(bestEleven);
    }

    @Transactional(readOnly = true)
    public List<BestElevenResponseDto> getMyList(Long userId) {
        return bestElevenRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BestElevenResponseDto getDetail(Long bestElevenId) {
        BestEleven bestEleven = bestElevenRepository.findByIdWithSlots(bestElevenId)
                .orElseThrow(() -> new CustomException("존재하지 않는 베스트11입니다."));
        return toResponse(bestEleven);
    }

    @Transactional
    public void delete(Long userId, Long bestElevenId) {
        BestEleven bestEleven = bestElevenRepository.findByIdAndUserId(bestElevenId, userId)
                .orElseThrow(() -> new CustomException("본인 소유의 베스트11만 삭제할 수 있습니다."));
        bestElevenRepository.delete(bestEleven);
    }

    // --- private helpers ---

    private void validateSlotsMatchFormation(List<SlotRequestDto> slots, Formation formation) {
        Set<String> requestCodes = slots.stream().map(SlotRequestDto::positionCode).collect(Collectors.toSet());
        Set<String> formationCodes = extractPositionCodes(formation);

        if (!requestCodes.equals(formationCodes)) {
            throw new CustomException("선택한 포메이션의 포지션 구성과 일치하지 않습니다.");
        }
    }

    private Set<String> extractPositionCodes(Formation formation) {
        // FormationService의 파싱 로직과 중복되므로, 실제로는 공통 유틸/컴포넌트로 추출 권장
        try {
            var layout = new ObjectMapper().readValue(
                    formation.getPositionLayout(), new TypeReference<List<Map<String, Object>>>() {}
            );
            return layout.stream().map(m -> (String) m.get("code")).collect(Collectors.toSet());
        } catch (JsonProcessingException e) {
            throw new CustomException("포메이션 데이터 파싱 오류", e);
        }
    }

    private List<BestElevenSlot> buildSlots(List<SlotRequestDto> slotRequests) {
        return slotRequests.stream().map(sr -> {
            Player player = playerRepository.findById(sr.playerId())
                    .orElseThrow(() -> new CustomException("존재하지 않는 선수입니다."));
            return BestElevenSlot.builder().positionCode(sr.positionCode()).player(player).build();
        }).toList();
    }

    private BestElevenResponseDto toResponse(BestEleven bestEleven) {
        List<SlotResponseDto> slots = bestEleven.getSlots().stream()
                .map(slot -> new SlotResponseDto(
                        slot.getPositionCode(),
                        new PlayerSummaryResponseDto(
                                slot.getPlayer().getId(), slot.getPlayer().getName(),
                                slot.getPlayer().getPrimaryPosition(), slot.getPlayer().getPhotoUrl()
                        )
                )).toList();

        return new BestElevenResponseDto(
                bestEleven.getId(), bestEleven.getTitie(), bestEleven.getFormation().getId(),
                slots, bestEleven.getCreatedAt()
        );
    }

}
