package com.grepp.spring.infra.response;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    OK("0000", HttpStatus.OK, "정상적으로 완료되었습니다."),
    CREATED("0001", HttpStatus.CREATED, "정상적으로 추가되었습니다."),
    BAD_REQUEST("4000", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_FILENAME("4001", HttpStatus.BAD_REQUEST, "사용 할 수 없는 파일 이름입니다."),
    UNAUTHORIZED("4010", HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    BAD_CREDENTIAL("4011", HttpStatus.UNAUTHORIZED, "아이디나 비밀번호가 틀렸습니다."),
    NOT_FOUND("4040", HttpStatus.NOT_FOUND, "NOT FOUND"),
    NOT_EXIST_PRE_AUTH_CREDENTIAL("4012", HttpStatus.OK, "사전 인증 정보가 요청에서 발견되지 않았습니다."),
    INTERNAL_SERVER_ERROR("5000", HttpStatus.INTERNAL_SERVER_ERROR, "서버에러 입니다."),
    SECURITY_INCIDENT("6000", HttpStatus.OK, "비정상적인 로그인 시도가 감지되었습니다."),
    NOT_FOUND_MEMBER("MEMBER_001",HttpStatus.NOT_FOUND,"해당 사용자를 찾을 수 없습니다"),
    NOT_FOUND_DETAIL("DETAIL_001", HttpStatus.NOT_FOUND,"해당 지출내역을 찾을 수 없습니다"),
    NOT_FOUND_CHALLENGE("CHALLENGE_001",HttpStatus.NOT_FOUND,"해당 챌린지를 찾을 수 없습니다"),
    ALREADY_REGISTERED_NO_EXPENSE("NO_EXPENSE_001", HttpStatus.CONFLICT,"오늘은 이미 지출 없음 등록이 되어있습니다."),
    ALREADY_REGISTERED_EXPENSE("EXPENSE_001", HttpStatus.CONFLICT, "오늘은 지출이 등록되어있습니다. 확인해주세요."),
    // 회원 관련 에러 코드
    EMAIL_ALREADY_EXISTS("MEMBER_002", HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
    NICKNAME_ALREADY_EXISTS("MEMBER_003", HttpStatus.BAD_REQUEST, "이미 사용중인 닉네임입니다."),
    PASSWORD_MISMATCH("MEMBER_004", HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    EMAIL_NOT_VERIFIED("MEMBER_005", HttpStatus.BAD_REQUEST, "이메일 인증이 필요합니다."),
    INVALID_INVITE_CODE("MEMBER_006", HttpStatus.BAD_REQUEST, "유효하지 않은 초대코드입니다."),
    INVALID_PASSWORD_FORMAT("MEMBER_007", HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상, 영문/숫자/특수문자를 모두 포함해야 합니다."),
    INVALID_TITLE_ID("MEMBER_008", HttpStatus.BAD_REQUEST, "유효하지 않은 칭호 ID입니다."),
    INVALID_EMAIL_CODE("MEMBER_009", HttpStatus.BAD_REQUEST, "인증 코드가 올바르지 않거나 만료되었습니다.");
    
    private final String code;
    private final HttpStatus status;
    private final String message;
    
    ResponseCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
    
    public String code() {
        return code;
    }
    
    public HttpStatus status() {
        return status;
    }
    
    public String message() {
        return message;
    }
}
