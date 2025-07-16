package com.grepp.spring.infra.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoUserInfoResponse {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("connected_at")
    private String connectedAt;
    
    @JsonProperty("properties")
    private Properties properties;
    
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;
    
    @Data
    public static class Properties {
        @JsonProperty("nickname")
        private String nickname;
        
        @JsonProperty("profile_image")
        private String profileImage;
        
        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }
    
    @Data
    public static class KakaoAccount {
        @JsonProperty("profile_nickname_needs_agreement")
        private Boolean profileNicknameNeedsAgreement;
        
        @JsonProperty("profile_image_needs_agreement")
        private Boolean profileImageNeedsAgreement;
        
        @JsonProperty("profile")
        private Profile profile;
        
        @JsonProperty("has_email")
        private Boolean hasEmail;
        
        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;
        
        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;
        
        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;
        
        @JsonProperty("email")
        private String email;
    }
    
    @Data
    public static class Profile {
        @JsonProperty("nickname")
        private String nickname;
        
        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;
        
        @JsonProperty("profile_image_url")
        private String profileImageUrl;
        
        @JsonProperty("is_default_image")
        private Boolean isDefaultImage;
    }
} 