package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.achieved_title.model.AchievedTitleDTO;
import com.grepp.spring.app.model.member.service.MemberService;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.dto.*;
import com.grepp.spring.app.model.place_bookmark.service.PlaceBookmarkService;
import com.grepp.spring.app.model.community.service.CommunityService;
import com.grepp.spring.app.model.budget.service.BudgetService;
import com.grepp.spring.app.model.achieved_title.service.AchievedTitleService;
import com.grepp.spring.app.model.achieved_title.domain.AchievedTitle;
import com.grepp.spring.app.model.achieved_title.repos.AchievedTitleRepository;
import com.grepp.spring.app.model.invite_code.service.InviteCodeService;
import java.math.BigDecimal;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import com.grepp.spring.infra.error.exceptions.CommonException;
import jakarta.validation.Valid;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.grepp.spring.app.model.auth.AuthService;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.service.EmailVerificationService;
import com.grepp.spring.app.model.auth.service.EmailService;
import com.grepp.spring.app.model.auth.token.UserBlackListRepository;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.entity.UserBlackList;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.app.model.auth.code.AuthToken;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.grepp.spring.app.model.challenge.service.ChallengeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/api/members")
@Tag(name = "멤버 API", description = "멤버 관련 API")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;
    private final UserBlackListRepository userBlackListRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PlaceBookmarkService placeBookmarkService;
    private final CommunityService communityService;
    private final BudgetService budgetService;
    private final AchievedTitleService achievedTitleService;
    private final AchievedTitleRepository achievedTitleRepository;
    private final InviteCodeService inviteCodeService;
    private final ChallengeService challengeService;

    public MemberController(MemberService memberService, MemberRepository memberRepository, PasswordEncoder passwordEncoder, AuthService authService, EmailVerificationService emailVerificationService, EmailService emailService, UserBlackListRepository userBlackListRepository, RefreshTokenService refreshTokenService, JwtTokenProvider jwtTokenProvider, PlaceBookmarkService placeBookmarkService, CommunityService communityService, BudgetService budgetService, AchievedTitleService achievedTitleService, AchievedTitleRepository achievedTitleRepository, InviteCodeService inviteCodeService, ChallengeService challengeService) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
        this.emailService = emailService;
        this.userBlackListRepository = userBlackListRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.placeBookmarkService = placeBookmarkService;
        this.communityService = communityService;
        this.budgetService = budgetService;
        this.achievedTitleService = achievedTitleService;
        this.achievedTitleRepository = achievedTitleRepository;
        this.inviteCodeService = inviteCodeService;
        this.challengeService = challengeService;
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MemberSignupResponse>> signup(@RequestBody @Valid MemberSignupRequest request) {
        // 이메일, 닉네임 중복 체크 (활성화된 계정만 체크)
        if (memberRepository.existsByEmailIgnoreCaseAndActivatedTrue(request.getEmail())) {
            throw new CommonException(ResponseCode.EMAIL_ALREADY_EXISTS);
        }
        if (memberRepository.findAll().stream().anyMatch(m -> m.getNickname().equalsIgnoreCase(request.getNickname()))) {
            throw new CommonException(ResponseCode.NICKNAME_ALREADY_EXISTS);
        }
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new CommonException(ResponseCode.PASSWORD_MISMATCH);
        }
        
        // 이메일 인증 확인
        if (!emailVerificationService.isEmailVerified(request.getEmail())) {
            throw new CommonException(ResponseCode.EMAIL_NOT_VERIFIED);
        }
        
        // 초대코드 검증 (선택적)
        if (request.getInviteCode() != null && !request.getInviteCode().trim().isEmpty()) {
            if (!inviteCodeService.isValidInviteCode(request.getInviteCode())) {
                throw new CommonException(ResponseCode.INVALID_INVITE_CODE);
            }
        }
        
        // 탈퇴한 계정이 있는지 확인
        Optional<Member> existingMember = memberRepository.findByEmailIgnoreCase(request.getEmail());
        Long userId;
        
        if (existingMember.isPresent() && !existingMember.get().getActivated()) {
            // 탈퇴한 계정이 있으면 기존 계정을 재활용
            Member member = existingMember.get();
            member.setPassword(passwordEncoder.encode(request.getPassword()));
            member.setName(request.getName());
            member.setNickname(request.getNickname());
            member.setPhoneNumber(request.getPhoneNumber());
            member.setActivated(true); // 계정 활성화
            member.setLevel(1); // 레벨 초기화
            member.setTotalExp(0); // 경험치 초기화
            member.setGoalAmount(null); // 목표 초기화
            member.setGoalStuff(null);
            member.setEquippedTitle(null); // 장착된 칭호 초기화
            member.setKakaoId(null); // 소셜 정보 초기화
            member.setSocialEmail(null);
            member.setProfileImage(null);
            memberRepository.save(member);
            userId = member.getMemberId();
        } else {
            // 새로운 회원 생성
            Member member = new Member();
            member.setEmail(request.getEmail());
            member.setPassword(passwordEncoder.encode(request.getPassword())); // 비밀번호 암호화
            member.setName(request.getName());
            member.setNickname(request.getNickname());
            member.setPhoneNumber(request.getPhoneNumber());
            member.setRole("ROLE_USER");
            userId = memberService.create(memberService.mapToDTO(member, new com.grepp.spring.app.model.member.model.MemberDTO()));
        }
        
        // 초대코드 사용 처리 (초대코드가 있는 경우에만)
        if (request.getInviteCode() != null && !request.getInviteCode().trim().isEmpty()) {
            inviteCodeService.useInviteCode(request.getInviteCode());
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successToCreate(new MemberSignupResponse(userId)));
    }

    // 이메일 중복확인
    @PostMapping("/email/check")
    public ResponseEntity<ApiResponse<MemberCheckResponse>> checkEmail(@RequestBody MemberCheckEmailRequest request) {
        boolean exists = memberRepository.existsByEmailIgnoreCaseAndActivatedTrue(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(new MemberCheckResponse(!exists)));
    }

    // 닉네임 중복확인
    @PostMapping("/nickname/check")
    public ResponseEntity<ApiResponse<MemberCheckResponse>> checkNickname(@RequestBody MemberCheckNicknameRequest request) {
        boolean exists = memberRepository.findAll().stream().anyMatch(m -> m.getNickname().equalsIgnoreCase(request.getNickname()));
        return ResponseEntity.ok(ApiResponse.success(new MemberCheckResponse(!exists)));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberLoginResponse>> login(@RequestBody @Valid MemberLoginRequest request, HttpServletResponse response) {
        System.out.println("로그인 시도: email=" + request.getEmail() + ", password=" + request.getPassword());
        com.grepp.spring.app.controller.api.mock.auth.payload.LoginRequest authRequest = new com.grepp.spring.app.controller.api.mock.auth.payload.LoginRequest();
        authRequest.setUsername(request.getEmail());
        authRequest.setPassword(request.getPassword());
        try {
            TokenDto tokenDto = authService.signin(authRequest);

            // 사용자 정보 조회 (role 포함)
            Member member = memberRepository.findByEmailIgnoreCase(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 쿠키 설정 - CORS 및 보안 설정 추가
            String accessTokenCookie = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(), tokenDto.getAccessToken(), tokenDto.getExpiresIn()).toString();
            String refreshTokenCookie = TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(), tokenDto.getRefreshToken(), tokenDto.getRefreshExpiresIn() * 1000).toString(); // 초를 밀리초로 변환

            // SameSite=None, Secure=true 설정으로 Cross-Origin 쿠키 전송 보장
            accessTokenCookie += "; SameSite=None; Secure";
            refreshTokenCookie += "; SameSite=None; Secure";

            response.addHeader("Set-Cookie", accessTokenCookie);
            response.addHeader("Set-Cookie", refreshTokenCookie);

            MemberLoginResponse.Data data = new MemberLoginResponse.Data(
                tokenDto.getAccessToken(),
                tokenDto.getRefreshToken(),
                tokenDto.getGrantType(),
                tokenDto.getExpiresIn(),
                tokenDto.getRefreshExpiresIn(),
                member.getRole()
            );
            return ResponseEntity.ok(ApiResponse.success(new MemberLoginResponse(data)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.BAD_CREDENTIAL.code(), "이메일 또는 비밀번호가 올바르지 않습니다.", null));
        }
    }

    // 아이디(이메일) 찾기
    @PostMapping("/email/find")
    public ResponseEntity<ApiResponse<MemberFindEmailResponse>> findEmail(@RequestBody @Valid MemberFindEmailRequest request) {
        // 이름과 휴대폰번호로 이메일 찾기 (여러 개 가능)
        java.util.List<Member> members = memberRepository.findByNameAndPhoneNumber(request.getName(), request.getPhoneNumber());
        
        if (members.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(ResponseCode.NOT_FOUND.code(), "해당 정보로 가입된 계정을 찾을 수 없습니다.", null));
        }
        
        // 이메일 마스킹 처리 (보안상 일부만 노출)
        java.util.List<String> maskedEmails = members.stream()
                .map(member -> maskEmail(member.getEmail()))
                .toList();
        
        MemberFindEmailResponse response = new MemberFindEmailResponse(maskedEmails);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 비밀번호 찾기 (임시 비밀번호 발송)
    @PostMapping("/password/find")
    public ResponseEntity<ApiResponse<MemberPasswordFindResponse>> findPassword(@RequestBody @Valid MemberPasswordFindRequest request) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmailIgnoreCase(request.getEmail())
                .orElse(null);
        
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(ResponseCode.NOT_FOUND.code(), "해당 이메일로 가입된 계정을 찾을 수 없습니다.", null));
        }
        
        // 임시 비밀번호 생성 및 발송
        String tempPassword = generateTempPassword();
        member.setPassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);
        
        // 이메일 발송
        try {
            emailService.sendTempPasswordEmail(member.getEmail(), tempPassword);
        } catch (Exception e) {
            // 이메일 발송 실패 시 비밀번호 롤백
            member.setPassword(passwordEncoder.encode(generateTempPassword())); // 기존 비밀번호로 복원 불가능하므로 새로운 임시 비밀번호 생성
            memberRepository.save(member);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ResponseCode.INTERNAL_SERVER_ERROR.code(), "이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.", null));
        }
        
        MemberPasswordFindResponse response = new MemberPasswordFindResponse();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<MemberLogoutResponse>> logout(HttpServletRequest request, HttpServletResponse response) {
        // JWT에서 현재 사용자 이메일 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // RefreshToken 삭제
        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
        if (accessToken != null) {
            try {
                io.jsonwebtoken.Claims claims = jwtTokenProvider.getClaims(accessToken);
                refreshTokenService.deleteByAccessTokenId(claims.getId());
            } catch (Exception e) {
                // 토큰 파싱 실패 시 무시
            }
        }
        
        // 사용자를 블랙리스트에 추가
        userBlackListRepository.save(new UserBlackList(currentEmail));
        
        // SecurityContext 클리어
        SecurityContextHolder.clearContext();
        
        // Cross-Origin 환경에서 쿠키 삭제를 보장하기 위한 설정
        String expiredAccessToken = TokenCookieFactory.createExpiredToken(AuthToken.ACCESS_TOKEN.name()).toString();
        String expiredRefreshToken = TokenCookieFactory.createExpiredToken(AuthToken.REFRESH_TOKEN.name()).toString();
        
        // SameSite=None, Secure=true 설정 추가로 Cross-Origin 쿠키 삭제 보장
        expiredAccessToken += "; SameSite=None; Secure";
        expiredRefreshToken += "; SameSite=None; Secure";
        
        response.addHeader("Set-Cookie", expiredAccessToken);
        response.addHeader("Set-Cookie", expiredRefreshToken);
        
        MemberLogoutResponse logoutResponse = new MemberLogoutResponse();
        return ResponseEntity.ok(ApiResponse.success(logoutResponse));
    }

    @GetMapping("/mypage/posts")
    @Operation(summary = "내가 작성한 게시글 조회", description = "현재 로그인한 사용자가 작성한 게시글 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // JWT에서 현재 사용자 ID 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        // 내가 작성한 게시글 조회
        List<Map<String, Object>> myPosts = communityService.getMyPosts(member.getMemberId());
        return ResponseEntity.ok(ApiResponse.success(myPosts));
    }

    // 장소 북마크 조회
    @GetMapping("/bookmarks/places")
    @Operation(summary = "장소 북마크 조회", description = "현재 로그인한 사용자의 장소 북마크 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPlaceBookmarks() {
        // JWT에서 현재 사용자 ID 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        List<Map<String, Object>> bookmarks = placeBookmarkService.getMemberPlaceBookmarks(member.getMemberId());
        return ResponseEntity.ok(ApiResponse.success(bookmarks));
    }
    
    @GetMapping("/bookmarks/posts")
    @Operation(summary = "게시글 북마크 조회", description = "현재 로그인한 사용자의 게시글 북마크 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getBookmarkedPosts() {
        // JWT에서 현재 사용자 ID 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        // 게시글 북마크 조회
        List<Map<String, Object>> bookmarkedPosts = communityService.getBookmarkedPosts(member.getMemberId());
        
        return ResponseEntity.ok(ApiResponse.success(bookmarkedPosts));
    }

    // 장소 북마크 해제
    @PatchMapping("/bookmarks/places/{place-id}")
    @Operation(summary = "장소 북마크 해제", description = "특정 장소의 북마크를 해제합니다.")
    public ResponseEntity<ApiResponse<MemberUnbookmarkResponse>> unbookmarkPlace(
            @PathVariable("place-id") Long placeId,
            @RequestBody MemberUnbookmarkRequest request) {
        // JWT에서 현재 사용자 ID 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        placeBookmarkService.unbookmarkPlace(member.getMemberId(), placeId, request.getPlaceType());
        
        MemberUnbookmarkResponse response = new MemberUnbookmarkResponse();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 마이페이지 조회
    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 조회", description = "현재 로그인한 사용자의 마이페이지 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberMypageResponse>> getMypage() {
        // JWT에서 현재 사용자 ID 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        // 레벨/경험치 계산
        int level = member.getLevel();
        int currentExp = member.getTotalExp() % 100; // 현재 레벨에서의 경험치
        int nextLevelExp = 100; // 고정값
        int expProgress = (int) ((double) currentExp / nextLevelExp * 100);
        
        // 실제 데이터 조회
        List<Map<String, Object>> myPosts = communityService.getMyPosts(member.getMemberId());
        List<Map<String, Object>> bookmarkedPosts = communityService.getBookmarkedPosts(member.getMemberId());
        List<Map<String, Object>> bookmarkedPlaces = placeBookmarkService.getMemberPlaceBookmarks(member.getMemberId());
        
        // 목표 정보 (실제 데이터 사용)
        String goalStuff = member.getGoalStuff();
        BigDecimal remainPrice = null;
        
        if (member.getGoalAmount() != null && member.getGoalStuff() != null) {
            // 현재 달의 총 수입과 총 지출 조회
            BigDecimal[] currentMonthTotal = budgetService.getCurrentMonthTotal(member.getMemberId());
            BigDecimal totalIncome = currentMonthTotal[0];
            BigDecimal totalExpense = currentMonthTotal[1];
            
            // 목표 달성까지 남은 금액 계산: goalAmount - (totalIncome - totalExpense)
            BigDecimal savedAmount = totalIncome.subtract(totalExpense);
            BigDecimal goalAmount = member.getGoalAmount();
            remainPrice = goalAmount.subtract(savedAmount);
            
            // 남은 금액이 음수면 0으로 설정 (목표 달성 완료)
            if (remainPrice.compareTo(BigDecimal.ZERO) < 0) {
                remainPrice = BigDecimal.ZERO;
            }
        }
        
        // 칭호 정보 (실제 서비스 데이터로 대체)
        List<AchievedTitleDTO> allTitles = achievedTitleRepository.findDtoByMemberId(member.getMemberId());
        List<Map<String, Object>> achievedTitles = new java.util.ArrayList<>();
        for (var t : allTitles) {
            Map<String, Object> map = new HashMap<>();
            map.put("aTId", t.getATId()); // aTId 추가
            map.put("challengeId", t.getChallengeId());
            map.put("name", t.getName());
            map.put("minCount", t.getMinCount());
            map.put("icon",t.getIcon());
            map.put("achieved", t.getAchieved()); // achieved 추가
            achievedTitles.add(map);
        }
        //Map<String, Object> equippedTitle = achievedTitles.isEmpty() ? null : achievedTitles.get(0);
        Optional<Member> withEquippedTitleAndChallenge = memberRepository.findWithEquippedTitleAndChallenge(member.getMemberId());

        AchievedTitleDTO equippedTitleDto = withEquippedTitleAndChallenge
            .map(Member::getEquippedTitle)
            .filter(Objects::nonNull)
            .map(equippedTitle -> {
                AchievedTitleDTO dto = new AchievedTitleDTO(
                    equippedTitle.getChallenge().getChallengeId(),
                    equippedTitle.getName(),
                    equippedTitle.getMinCount(),
                    equippedTitle.getIcon()
                );
                dto.setATId(equippedTitle.getATId()); // aTId 설정
                dto.setAchieved(equippedTitle.getAchieved()); // achieved 설정
                return dto;
            })
            .orElse(null);

        MemberMypageResponse.Data data = new MemberMypageResponse.Data(
            member.getMemberId(),
            member.getEmail(),
            member.getName(),
            member.getNickname(),
            member.getProfileImage(),
            level,
            currentExp,
            nextLevelExp,
            expProgress,
            myPosts,
            goalStuff,
            remainPrice,
            bookmarkedPosts,
            bookmarkedPlaces,
            equippedTitleDto,
            achievedTitles
        );
        
        MemberMypageResponse response = new MemberMypageResponse(data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }



    // 목표 금액/항목 설정
    @PatchMapping("/mypage/goal")
    @Operation(summary = "목표 금액/항목 설정", description = "사용자의 목표 금액과 항목을 설정합니다.")
    public ResponseEntity<ApiResponse<MemberGoalResponse>> setGoal(@RequestBody @Valid MemberGoalRequest request) {
        // JWT에서 현재 사용자 ID 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        // 목표 정보 업데이트
        if (request.getGoalAmount() != null) {
            member.setGoalAmount(request.getGoalAmount());
        } else {
            member.setGoalAmount(null);
        }
        member.setGoalStuff(request.getGoalStuff());
        memberRepository.save(member);
        
        MemberGoalResponse response = new MemberGoalResponse(request.getGoalAmount(), request.getGoalStuff());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 목표 금액/항목 조회
    @GetMapping("/mypage/goal")
    @Operation(summary = "목표 금액/항목 조회", description = "사용자의 목표 금액과 항목을 조회합니다.")
    public ResponseEntity<ApiResponse<MemberGoalResponse>> getGoal() {
        // JWT에서 현재 사용자 ID 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        BigDecimal goalAmount = member.getGoalAmount();
        MemberGoalResponse response = new MemberGoalResponse(goalAmount, member.getGoalStuff());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 프로필 수정
    @PatchMapping("/mypage/profile")
    @Operation(summary = "프로필 수정", description = "사용자의 프로필 정보(닉네임, 프로필 이미지, 비밀번호)를 수정합니다.")
    public ResponseEntity<ApiResponse<Object>> updateProfile(@RequestBody @Valid MemberProfileUpdateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        // 닉네임 변경
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            member.setNickname(request.getNickname());
        }
        // 프로필 이미지 변경
        if (request.getProfileImage() != null) {
            member.setProfileImage(request.getProfileImage());
        }
        // 비밀번호 변경 (기존 비밀번호 입력 없이도 가능)
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            // 새 비밀번호 유효성 검사
            if (!isValidPassword(request.getNewPassword())) {
                throw new CommonException(ResponseCode.INVALID_PASSWORD_FORMAT);
            }
            // 새 비밀번호 확인
            if (!request.getNewPassword().equals(request.getNewPasswordCheck())) {
                throw new CommonException(ResponseCode.PASSWORD_MISMATCH);
            }
            member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        memberRepository.save(member);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 칭호 장착
    @PatchMapping("/titles/equip")
    @Operation(summary = "칭호 장착", description = "획득한 칭호 중 atId로 지정한 칭호를 장착합니다.")
    public ResponseEntity<ApiResponse<MemberEquipTitleResponse>> equipTitle(@RequestBody @Valid MemberEquipTitleRequest request, HttpServletRequest httpRequest) {
        // 디버깅: 요청 데이터 로그
        System.out.println("[EquipTitle] 요청 받은 aTId: " + request.getATId());
        System.out.println("[EquipTitle] 요청 객체 전체: " + request);
        System.out.println("[EquipTitle] Content-Type: " + httpRequest.getContentType());
        System.out.println("[EquipTitle] 요청 메서드: " + httpRequest.getMethod());
        
        // aTId 유효성 검증
        if (!request.isValidATId()) {
            throw new CommonException(ResponseCode.INVALID_TITLE_ID);
        }
        
        // JWT에서 현재 사용자 ID 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        // 해당 칭호가 회원이 획득한 칭호인지 확인
        AchievedTitle achievedTitle = achievedTitleRepository.findById(request.getATId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 칭호입니다."));
        if (!achievedTitle.getMember().getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("획득하지 않은 칭호입니다.");
        }
        
        // 칭호 장착
        member.setEquippedTitle(achievedTitle);
        memberRepository.save(member);
        
        // 응답 데이터 구성
        String equippedTitleName = achievedTitle.getName();
        
        // 획득한 칭호 목록 조회
        List<AchievedTitle> allTitles = achievedTitleRepository.findAll();
        List<AchievedTitle> achievedTitles = allTitles.stream()
                .filter(title -> title.getMember().getMemberId().equals(member.getMemberId()) && title.getAchieved())
                .collect(java.util.stream.Collectors.toList());
        
        List<String> achievedTitleNames = achievedTitles.stream()
                .map(AchievedTitle::getName)
                .collect(java.util.stream.Collectors.toList());
        
        MemberEquipTitleResponse response = new MemberEquipTitleResponse(equippedTitleName, achievedTitleNames);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 칭호 해제
    @DeleteMapping("/titles/unequip")
    @Operation(summary = "칭호 해제", description = "현재 장착된 칭호를 해제합니다.")
    public ResponseEntity<ApiResponse<Object>> unequipTitle() {
        // JWT에서 현재 사용자 ID 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        // 현재 장착된 칭호 확인
        AchievedTitle equippedTitle = member.getEquippedTitle();
        if (equippedTitle == null) {
            throw new IllegalArgumentException("장착된 칭호가 없습니다.");
        }
        
        // 칭호 해제
        member.setEquippedTitle(null);
        memberRepository.save(member);
        
        return ResponseEntity.ok(ApiResponse.success("칭호가 해제되었습니다."));
    }

    // 획득한 칭호 조회
    @GetMapping("/titles/achieved")
    @Operation(summary = "획득한 칭호 조회", description = "현재 로그인한 사용자가 획득한 칭호 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<MemberTitleResponse>>> getAchievedTitles() {
        // JWT에서 현재 사용자 ID 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        // 획득한 칭호 목록 조회
        List<AchievedTitle> allTitles = achievedTitleRepository.findAll();
        List<AchievedTitle> achievedTitles = allTitles.stream()
                .filter(title -> title.getMember().getMemberId().equals(member.getMemberId()) && title.getAchieved())
                .collect(java.util.stream.Collectors.toList());
        
        List<MemberTitleResponse> titleResponses = achievedTitles.stream()
                .map(title -> new MemberTitleResponse(
                        title.getATId(),
                        title.getName(),
                        title.getAchieved(),
                        title.getMinCount(),
                        title.getChallenge().getChallengeId(),
                        title.getChallenge().getName()
                ))
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(titleResponses));
    }

    // 장착된 칭호 조회
    @GetMapping("/titles/equipped")
    @Operation(summary = "장착된 칭호 조회", description = "현재 로그인한 사용자가 장착한 칭호를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberTitleResponse>> getEquippedTitle() {
        // JWT에서 현재 사용자 ID 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        // 장착된 칭호 조회
        AchievedTitle equippedTitle = member.getEquippedTitle();

        if (equippedTitle == null) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }
        
        MemberTitleResponse titleResponse = new MemberTitleResponse(
                equippedTitle.getATId(),
                equippedTitle.getName(),
                equippedTitle.getAchieved(),
                equippedTitle.getMinCount(),
                equippedTitle.getChallenge().getChallengeId(),
                equippedTitle.getChallenge().getName()
        );
        
        return ResponseEntity.ok(ApiResponse.success(titleResponse));
    }

    // 내 초대 코드 복사
    @GetMapping("/invite-code")
    @Operation(summary = "내 초대 코드 복사", description = "현재 로그인한 사용자의 초대 코드를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberInviteCodeResponse>> getInviteCode() {
        // JWT에서 현재 사용자 이메일 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        // 초대 코드 생성 (멤버 ID 기반)
        String inviteCode = inviteCodeService.generateInviteCode(member.getMemberId());
        
        MemberInviteCodeResponse response = new MemberInviteCodeResponse(inviteCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 회원 탈퇴
    @PatchMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 회원 탈퇴를 처리합니다.")
    public ResponseEntity<ApiResponse<MemberWithdrawResponse>> withdrawMember(HttpServletResponse response) {
        // JWT에서 현재 사용자 이메일 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 이메일로 멤버 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        // 회원 탈퇴 처리
        memberService.withdrawMember(member.getMemberId());

        // 사용자를 블랙리스트에 추가 (로그아웃과 동일한 처리)
        userBlackListRepository.save(new UserBlackList(currentEmail));
        
        // SecurityContext 클리어
        SecurityContextHolder.clearContext();
        
        // 쿠키 만료 처리 (로그아웃과 동일한 처리)
        ResponseCookie expiredAccessToken = TokenCookieFactory.createExpiredToken(AuthToken.ACCESS_TOKEN.name());
        ResponseCookie expiredRefreshToken = TokenCookieFactory.createExpiredToken(AuthToken.REFRESH_TOKEN.name());
        
        response.addHeader("Set-Cookie", expiredAccessToken.toString());
        response.addHeader("Set-Cookie", expiredRefreshToken.toString());

        MemberWithdrawResponse withdrawResponse = new MemberWithdrawResponse("회원 탈퇴가 완료되었습니다.", "/");
        return ResponseEntity.ok(ApiResponse.success(withdrawResponse));
    }

    // 챌린지 대시보드 조회
    @GetMapping("/mypage/challenges/dashboard")
    @Operation(summary = "챌린지 대시보드 조회", description = "현재 로그인한 사용자의 챌린지 대시보드 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberChallengeDashboardResponse>> getChallengeDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        // 실제 챌린지 서비스에서 데이터 조회
        List<com.grepp.spring.app.model.challenge.model.ChallengeStatusDto> challengeStatuses = challengeService.getChallengeStatuses(member.getMemberId());
        List<MemberChallengeDashboardResponse.ChallengeData> challenges = challengeStatuses.stream()
            .map(dto -> new MemberChallengeDashboardResponse.ChallengeData(
                dto.getChallengeId(),
                dto.getName(), // title -> name
                dto.getType(),
                dto.getDescription(),
                dto.getTotal(),
                dto.getProgress(),
                dto.getIcon()
            )).toList();
        MemberChallengeDashboardResponse response = new MemberChallengeDashboardResponse(challenges);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{memberId}/challenges/dashboard")
    @Operation(summary = "다른 유저 챌린지 대시보드 조회", description = "다른 유저의 챌린지 대시보드 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberChallengeDashboardResponse>> getmemberChallengeDashboard(@PathVariable Long memberId) {
        List<com.grepp.spring.app.model.challenge.model.ChallengeStatusDto> challengeStatuses = challengeService.getChallengeStatuses(memberId);
        List<MemberChallengeDashboardResponse.ChallengeData> challenges = challengeStatuses.stream()
            .map(dto -> new MemberChallengeDashboardResponse.ChallengeData(
                dto.getChallengeId(),
                dto.getName(), // title -> name
                dto.getType(),
                dto.getDescription(),
                dto.getTotal(),
                dto.getProgress(),
                dto.getIcon()
            )).toList();
        MemberChallengeDashboardResponse response = new MemberChallengeDashboardResponse(challenges);
        return ResponseEntity.ok(ApiResponse.success(response));
    }




    //다른 유저 프로필 조회
    @GetMapping("/profile/{memberId}")
    @Operation(summary = "다른 유저 프로필 조회", description = "memberId로 다른 유저의 프로필 정보를 조회합니다. 마이페이지와 동일한 데이터 구조를 반환합니다.")
    public ResponseEntity<ApiResponse<MemberMypageResponse>> getOtherMemberProfile(@PathVariable Long memberId) {
        // JWT 인증 필요(로그인 사용자만 조회 가능)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 현재 로그인한 사용자 정보 가져오기
        String currentUserEmail = auth.getName();
        Member currentUser = memberRepository.findByEmailIgnoreCase(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("현재 로그인한 사용자를 찾을 수 없습니다."));
        
        // memberId로 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        // 마이페이지와 동일한 데이터 구성
        int level = member.getLevel();
        int currentExp = member.getTotalExp() % 100;
        int nextLevelExp = 100;
        int expProgress = (int) ((double) currentExp / nextLevelExp * 100);
        
        // 다른 유저의 게시글과 북마크 정보 조회
        List<Map<String, Object>> myPosts = communityService.getMyPosts(member.getMemberId());
        List<Map<String, Object>> bookmarkedPosts = communityService.getBookmarkedPosts(member.getMemberId());
        List<Map<String, Object>> bookmarkedPlaces = placeBookmarkService.getMemberPlaceBookmarks(member.getMemberId());
        
        // 현재 로그인한 사용자의 북마크 상태를 각 게시글에 추가
        List<Map<String, Object>> myPostsWithBookmarkStatus = addBookmarkAndLikeStatusToPosts(myPosts, currentUser.getMemberId());
        List<Map<String, Object>> bookmarkedPostsWithBookmarkStatus = addBookmarkAndLikeStatusToPosts(bookmarkedPosts, currentUser.getMemberId());
        List<Map<String, Object>> bookmarkedPlacesWithBookmarkStatus = addBookmarkStatusToPlaces(bookmarkedPlaces, currentUser.getMemberId());
        
        String goalStuff = member.getGoalStuff();
        BigDecimal remainPrice = null;
        if (member.getGoalAmount() != null && member.getGoalStuff() != null) {
            BigDecimal[] currentMonthTotal = budgetService.getCurrentMonthTotal(member.getMemberId());
            BigDecimal totalIncome = currentMonthTotal[0];
            BigDecimal totalExpense = currentMonthTotal[1];
            BigDecimal savedAmount = totalIncome.subtract(totalExpense);
            BigDecimal goalAmount = member.getGoalAmount();
            remainPrice = goalAmount.subtract(savedAmount);
            if (remainPrice.compareTo(BigDecimal.ZERO) < 0) {
                remainPrice = BigDecimal.ZERO;
            }
        }
        List<AchievedTitleDTO> allTitles = achievedTitleRepository.findDtoByMemberId(memberId);
        List<Map<String, Object>> achievedTitles = new java.util.ArrayList<>();
            for (var t : allTitles) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("aTId", t.getATId()); // aTId 추가
                    map.put("challengeId", t.getChallengeId());
                    map.put("name", t.getName());
                    map.put("minCount", t.getMinCount());
                    map.put("icon",t.getIcon());
                    map.put("achieved", t.getAchieved()); // achieved 추가
                    achievedTitles.add(map);
            }


       // Map<String, Object> equippedTitle = achievedTitles.isEmpty() ? null : achievedTitles.get(0);
        Optional<Member> withEquippedTitleAndChallenge = memberRepository.findWithEquippedTitleAndChallenge(member.getMemberId());

        AchievedTitleDTO equippedTitleDto = withEquippedTitleAndChallenge
            .map(Member::getEquippedTitle)
            .filter(Objects::nonNull)
            .map(equippedTitle -> {
                AchievedTitleDTO dto = new AchievedTitleDTO(
                    equippedTitle.getChallenge().getChallengeId(),
                    equippedTitle.getName(),
                    equippedTitle.getMinCount(),
                    equippedTitle.getIcon()
                );
                dto.setATId(equippedTitle.getATId()); // aTId 설정
                dto.setAchieved(equippedTitle.getAchieved()); // achieved 설정
                return dto;
            })
            .orElse(null);

        MemberMypageResponse.Data data = new MemberMypageResponse.Data(
            member.getMemberId(),
            member.getEmail(),
            member.getName(),
            member.getNickname(),
            member.getProfileImage(),
            level,
            currentExp,
            nextLevelExp,
            expProgress,
            myPostsWithBookmarkStatus,
            goalStuff,
            remainPrice,
            bookmarkedPostsWithBookmarkStatus,
            bookmarkedPlacesWithBookmarkStatus,
            equippedTitleDto,
            achievedTitles
        );
        MemberMypageResponse response = new MemberMypageResponse(data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    // 게시글에 현재 사용자의 북마크 상태 추가
    private List<Map<String, Object>> addBookmarkAndLikeStatusToPosts(List<Map<String, Object>> posts, Long currentUserId) {
        return posts.stream().map(post -> {
            Map<String, Object> postWithBookmarkStatus = new HashMap<>(post);
            Long postId = Long.valueOf(post.get("postId").toString()); // postid → postId로 수정
            
            // 현재 사용자가 해당 게시글을 북마크했는지 확인
            boolean isBookmarkedByCurrentUser = communityService.isPostBookmarkedByUser(postId, currentUserId);
            postWithBookmarkStatus.put("isBookmarked", isBookmarkedByCurrentUser);
            
            // 현재 사용자가 해당 게시글을 좋아요했는지 확인
            boolean isLikedByCurrentUser = communityService.isPostLikedByUser(postId, currentUserId);
            postWithBookmarkStatus.put("isLiked", isLikedByCurrentUser);
            
            return postWithBookmarkStatus;
        }).toList();
    }
    
    // 장소에 현재 사용자의 북마크 상태 추가
    private List<Map<String, Object>> addBookmarkStatusToPlaces(List<Map<String, Object>> places, Long currentUserId) {
        return places.stream().map(place -> {
            Map<String, Object> placeWithBookmarkStatus = new HashMap<>(place);
            String placeId = place.get("storeId") != null ? place.get("storeId").toString() : 
                           place.get("festivalId") != null ? place.get("festivalId").toString() : 
                           place.get("libraryId") != null ? place.get("libraryId").toString() : null;
            String placeType = place.get("type").toString();
            
            if (placeId != null) {
                // 현재 사용자가 해당 장소를 북마크했는지 확인
                boolean isBookmarkedByCurrentUser = placeBookmarkService.isPlaceBookmarkedByUser(placeId, placeType, currentUserId);
                placeWithBookmarkStatus.put("isBookmarked", isBookmarkedByCurrentUser);
            }
            
            return placeWithBookmarkStatus;
        }).toList();
    }

    // ===== 유틸리티 메서드 =====
    
    // 이메일 마스킹 처리
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }
        
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 2) {
            return email;
        }
        
        String maskedLocal = localPart.substring(0, 2) + "*".repeat(localPart.length() - 2);
        return maskedLocal + "@" + domain;
    }
    
    // 임시 비밀번호 생성
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }

    // 비밀번호 정책 검증
    private boolean isValidPassword(String password) {
        if (password == null) return false;
        // 8자 이상, 영문/숫자/특수문자 포함
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");
    }




} 