package com.best11.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    SLOT_FORMATION_MISMATCH(HttpStatus.BAD_REQUEST, "선택한 포메이션의 포지션 구성과 일치하지 않습니다."),


    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),

    // 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    PLAYER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 선수입니다."),
    FORMATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 포메이션입니다."),
    BEST_ELEVEN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 베스트11입니다."),
    LEAGUE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리그입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 유저명입니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    FORMATION_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "포메이션 데이터 파싱 오류");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}