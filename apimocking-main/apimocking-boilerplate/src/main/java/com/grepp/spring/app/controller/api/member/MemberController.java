package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.achieved_title.model.AchievedTitleDTO;
import com.grepp.spring.app.model.member.service.MemberService;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.member.domain.Member;
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
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody @Valid SignupRequest request) {
        // 이메일, 닉네임 중복 체크
        if (memberRepository.existsByEmailIgnoreCase(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "이미 사용중인 이메일입니다.", null));
        }
        if (memberRepository.findAll().stream().anyMatch(m -> m.getNickname().equalsIgnoreCase(request.getNickname()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "이미 사용중인 닉네임입니다.", null));
        }
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "비밀번호가 일치하지 않습니다.", null));
        }
        
        // 이메일 인증 확인
        if (!emailVerificationService.isEmailVerified(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "이메일 인증이 필요합니다.", null));
        }
        
        // 초대코드 검증 (선택적)
        if (request.getInviteCode() != null && !request.getInviteCode().trim().isEmpty()) {
            if (!inviteCodeService.isValidInviteCode(request.getInviteCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "유효하지 않은 초대코드입니다.", null));
            }
        }
        
        // 회원 생성
        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setPassword(passwordEncoder.encode(request.getPassword())); // 비밀번호 암호화
        member.setName(request.getName());
        member.setNickname(request.getNickname());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setRole("ROLE_USER");
        Long userId = memberService.create(memberService.mapToDTO(member, new com.grepp.spring.app.model.member.model.MemberDTO()));
        
        // 초대코드 사용 처리 (초대코드가 있는 경우에만)
        if (request.getInviteCode() != null && !request.getInviteCode().trim().isEmpty()) {
            inviteCodeService.useInviteCode(request.getInviteCode());
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successToCreate(new SignupResponse(userId)));
    }

    // 이메일 중복확인
    @PostMapping("/email/check")
    public ResponseEntity<ApiResponse<CheckResponse>> checkEmail(@RequestBody CheckEmailRequest request) {
        boolean exists = memberRepository.existsByEmailIgnoreCase(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(new CheckResponse(!exists)));
    }

    // 닉네임 중복확인
    @PostMapping("/nickname/check")
    public ResponseEntity<ApiResponse<CheckResponse>> checkNickname(@RequestBody CheckNicknameRequest request) {
        boolean exists = memberRepository.findAll().stream().anyMatch(m -> m.getNickname().equalsIgnoreCase(request.getNickname()));
        return ResponseEntity.ok(ApiResponse.success(new CheckResponse(!exists)));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        System.out.println("로그인 시도: email=" + request.getEmail() + ", password=" + request.getPassword());
        com.grepp.spring.app.controller.api.mock.auth.payload.LoginRequest authRequest = new com.grepp.spring.app.controller.api.mock.auth.payload.LoginRequest();
        authRequest.setUsername(request.getEmail());
        authRequest.setPassword(request.getPassword());
        try {
            TokenDto tokenDto = authService.signin(authRequest);

            // accessToken 쿠키 생성
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", tokenDto.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(tokenDto.getExpiresIn() / 1000)
                .sameSite("None")
                .build();
            // refreshToken 쿠키 생성
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(tokenDto.getRefreshExpiresIn() / 1000)
                .sameSite("None")
                .build();
            response.addHeader("Set-Cookie", accessTokenCookie.toString());
            response.addHeader("Set-Cookie", refreshTokenCookie.toString());

            LoginResponse.Data data = new LoginResponse.Data(
                tokenDto.getAccessToken(),
                tokenDto.getRefreshToken(),
                tokenDto.getGrantType(),
                tokenDto.getExpiresIn(),
                tokenDto.getRefreshExpiresIn()
            );
            return ResponseEntity.ok(ApiResponse.success(new LoginResponse(data)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.BAD_CREDENTIAL.code(), "이메일 또는 비밀번호가 올바르지 않습니다.", null));
        }
    }

    // 아이디(이메일) 찾기
    @PostMapping("/email/find")
    public ResponseEntity<ApiResponse<FindEmailResponse>> findEmail(@RequestBody @Valid FindEmailRequest request) {
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
        
        FindEmailResponse response = new FindEmailResponse(maskedEmails);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 비밀번호 찾기 (임시 비밀번호 발송)
    @PostMapping("/password/find")
    public ResponseEntity<ApiResponse<PasswordFindResponse>> findPassword(@RequestBody @Valid PasswordFindRequest request) {
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
        
        PasswordFindResponse response = new PasswordFindResponse();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(HttpServletResponse response) {
        // JWT에서 현재 사용자 이메일 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        
        // 사용자를 블랙리스트에 추가
        userBlackListRepository.save(new UserBlackList(currentEmail));
        
        // SecurityContext 클리어
        SecurityContextHolder.clearContext();
        
        // 쿠키 만료 처리
        ResponseCookie expiredAccessToken = TokenCookieFactory.createExpiredToken(AuthToken.ACCESS_TOKEN.name());
        ResponseCookie expiredRefreshToken = TokenCookieFactory.createExpiredToken(AuthToken.REFRESH_TOKEN.name());
        
        response.addHeader("Set-Cookie", expiredAccessToken.toString());
        response.addHeader("Set-Cookie", expiredRefreshToken.toString());
        
        LogoutResponse logoutResponse = new LogoutResponse();
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
    public ResponseEntity<ApiResponse<UnbookmarkResponse>> unbookmarkPlace(
            @PathVariable("place-id") Long placeId,
            @RequestBody UnbookmarkRequest request) {
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
        
        UnbookmarkResponse response = new UnbookmarkResponse();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 마이페이지 조회
    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 조회", description = "현재 로그인한 사용자의 마이페이지 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MypageResponse>> getMypage() {
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
            map.put("challengeId", t.getChallengeId());
            map.put("name", t.getName());
            map.put("minCount", t.getMinCount());
            map.put("icon",t.getIcon());
            achievedTitles.add(map);
        }
        //Map<String, Object> equippedTitle = achievedTitles.isEmpty() ? null : achievedTitles.get(0);
        Optional<Member> withEquippedTitleAndChallenge = memberRepository.findWithEquippedTitleAndChallenge(member.getMemberId());

        AchievedTitleDTO equippedTitleDto = withEquippedTitleAndChallenge
            .map(Member::getEquippedTitle)
            .filter(Objects::nonNull)
            .map(equippedTitle -> new AchievedTitleDTO(
                equippedTitle.getChallenge().getChallengeId(),
                equippedTitle.getName(),
                equippedTitle.getMinCount(),
                equippedTitle.getIcon()
            ))
            .orElse(null);

        MypageResponse.Data data = new MypageResponse.Data(
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
        
        MypageResponse response = new MypageResponse(data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 목표 금액/항목 설정
    @PatchMapping("/mypage/goal")
    @Operation(summary = "목표 금액/항목 설정", description = "사용자의 목표 금액과 항목을 설정합니다.")
    public ResponseEntity<ApiResponse<GoalResponse>> setGoal(@RequestBody @Valid GoalRequest request) {
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
        
        GoalResponse response = new GoalResponse(request.getGoalAmount(), request.getGoalStuff());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 목표 금액/항목 조회
    @GetMapping("/mypage/goal")
    @Operation(summary = "목표 금액/항목 조회", description = "사용자의 목표 금액과 항목을 조회합니다.")
    public ResponseEntity<ApiResponse<GoalResponse>> getGoal() {
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
        GoalResponse response = new GoalResponse(goalAmount, member.getGoalStuff());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 프로필 수정
    @PatchMapping("/mypage/profile")
    @Operation(summary = "프로필 수정", description = "사용자의 프로필 정보(닉네임, 프로필 이미지, 비밀번호)를 수정합니다.")
    public ResponseEntity<ApiResponse<Object>> updateProfile(@RequestBody @Valid ProfileUpdateRequest request) {
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
        // 비밀번호 변경
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            // 기존 비밀번호 확인
            if (request.getOldPassword() == null || !passwordEncoder.matches(request.getOldPassword(), member.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "기존 비밀번호가 일치하지 않습니다.", null));
            }
            // 새 비밀번호 유효성 검사
            if (!isValidPassword(request.getNewPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "비밀번호는 8자 이상, 영문/숫자/특수문자를 모두 포함해야 합니다.", null));
            }
            // 새 비밀번호 확인
            if (!request.getNewPassword().equals(request.getNewPasswordCheck())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.", null));
            }
            member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        memberRepository.save(member);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 대표 칭호 변경
    @PatchMapping("/mypage/equipped-title")
    @Operation(summary = "대표 칭호 변경", description = "사용자의 대표 칭호를 변경합니다.")
    public ResponseEntity<ApiResponse<RepresentativeTitleResponse>> changeRepresentativeTitle(
            @RequestBody @Valid RepresentativeTitleRequest request) {
        
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
        AchievedTitle achievedTitle = achievedTitleRepository.findById(request.getTitleId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 칭호입니다."));
        if (!achievedTitle.getMember().getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("획득하지 않은 칭호입니다.");
        }
        
        // 대표 칭호로 설정
        member.setEquippedTitle(achievedTitle);
        memberRepository.save(member);
        
        RepresentativeTitleResponse response = new RepresentativeTitleResponse();
        response.setTitleId(request.getTitleId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 칭호 즉시 장착
    @PatchMapping("/titles/{challengeId}/equip")
    @Operation(summary = "칭호 즉시 장착", description = "특정 챌린지의 칭호를 즉시 장착합니다.")
    public ResponseEntity<ApiResponse<EquipTitleResponse>> equipTitleImmediately(@PathVariable Long challengeId) {
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
        
        // 해당 챌린지의 칭호가 회원이 획득한 칭호인지 확인
        List<AchievedTitle> allTitles = achievedTitleRepository.findAll();
        AchievedTitle targetTitle = allTitles.stream()
                .filter(title -> title.getMember().getMemberId().equals(member.getMemberId()) 
                        && title.getChallenge().getChallengeId().equals(challengeId)
                        && title.getAchieved())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 챌린지의 칭호를 획득하지 않았습니다."));
        
        // 칭호 장착
        member.setEquippedTitle(targetTitle);
        memberRepository.save(member);
        
        // 응답 데이터 구성
        String equippedTitleName = targetTitle.getName();
        
        // 획득한 칭호 목록 조회
        List<AchievedTitle> achievedTitles = allTitles.stream()
                .filter(title -> title.getMember().getMemberId().equals(member.getMemberId()) && title.getAchieved())
                .collect(java.util.stream.Collectors.toList());
        
        List<String> achievedTitleNames = achievedTitles.stream()
                .map(AchievedTitle::getName)
                .collect(java.util.stream.Collectors.toList());
        
        EquipTitleResponse response = new EquipTitleResponse(equippedTitleName, achievedTitleNames);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 칭호 해제
    @DeleteMapping("/titles/{challengeId}/unequip")
    @Operation(summary = "칭호 해제", description = "특정 챌린지의 칭호를 해제합니다.")
    public ResponseEntity<ApiResponse<Object>> unequipTitle(@PathVariable Long challengeId) {
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
        
        // 현재 장착된 칭호가 해당 챌린지의 칭호인지 확인
        AchievedTitle equippedTitle = member.getEquippedTitle();
        if (equippedTitle == null || !equippedTitle.getChallenge().getChallengeId().equals(challengeId)) {
            throw new IllegalArgumentException("해당 챌린지의 칭호가 장착되어 있지 않습니다.");
        }
        
        // 칭호 해제
        member.setEquippedTitle(null);
        memberRepository.save(member);
        
        return ResponseEntity.ok(ApiResponse.success("칭호가 해제되었습니다."));
    }

    // 획득한 칭호 조회
    @GetMapping("/titles/achieved")
    @Operation(summary = "획득한 칭호 조회", description = "현재 로그인한 사용자가 획득한 칭호 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<TitleResponse>>> getAchievedTitles() {
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
        
        List<TitleResponse> titleResponses = achievedTitles.stream()
                .map(title -> new TitleResponse(
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
    public ResponseEntity<ApiResponse<TitleResponse>> getEquippedTitle() {
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
        
        TitleResponse titleResponse = new TitleResponse(
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
    public ResponseEntity<ApiResponse<InviteCodeResponse>> getInviteCode() {
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
        
        InviteCodeResponse response = new InviteCodeResponse(inviteCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 회원 탈퇴
    @PatchMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 회원 탈퇴를 처리합니다.")
    public ResponseEntity<ApiResponse<WithdrawResponse>> withdrawMember() {
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
        
        // SecurityContext 클리어
        SecurityContextHolder.clearContext();
        
        WithdrawResponse response = new WithdrawResponse();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 챌린지 대시보드 조회
    @GetMapping("/mypage/challenges/dashboard")
    @Operation(summary = "챌린지 대시보드 조회", description = "현재 로그인한 사용자의 챌린지 대시보드 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ChallengeDashboardResponse>> getChallengeDashboard() {
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
        List<ChallengeDashboardResponse.ChallengeData> challenges = challengeStatuses.stream()
            .map(dto -> new ChallengeDashboardResponse.ChallengeData(
                dto.getChallengeId(),
                dto.getName(), // title -> name
                dto.getType(),
                dto.getDescription(),
                dto.getTotal(),
                dto.getProgress(),
                dto.getIcon()
            )).toList();
        ChallengeDashboardResponse response = new ChallengeDashboardResponse(challenges);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/social/extra")
    @Operation(summary = "소셜 회원 추가 정보 입력", description = "구글 로그인 후 추가 정보(닉네임, 휴대폰번호 등)를 입력받아 회원 정보를 완성합니다. JWT 인증 필요, email 등은 자동 추출.")
    public ResponseEntity<ApiResponse<Object>> completeSocialSignup(@RequestBody @Valid ExtraInfoRequest request) {
        // JWT에서 본인 email 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        // 회원 조회
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        // 닉네임/휴대폰번호 등만 업데이트
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            member.setNickname(request.getNickname());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            member.setPhoneNumber(request.getPhoneNumber());
        }
        memberRepository.save(member);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ExtraInfoRequest {
        @Schema(description = "닉네임", example = "소셜유저")
        private String nickname;
        @Schema(description = "휴대폰번호", example = "01012345678")
        private String phoneNumber;
    }

    @GetMapping("/profile/{memberId}")
    @Operation(summary = "다른 유저 프로필 조회", description = "memberId로 다른 유저의 프로필 정보를 조회합니다. 마이페이지와 동일한 데이터 구조를 반환합니다.")
    public ResponseEntity<ApiResponse<MypageResponse>> getOtherMemberProfile(@PathVariable Long memberId) {
        // JWT 인증 필요(로그인 사용자만 조회 가능)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        // memberId로 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        // 마이페이지와 동일한 데이터 구성
        int level = member.getLevel();
        int currentExp = member.getTotalExp() % 100;
        int nextLevelExp = 100;
        int expProgress = (int) ((double) currentExp / nextLevelExp * 100);
        List<Map<String, Object>> myPosts = communityService.getMyPosts(member.getMemberId());
        List<Map<String, Object>> bookmarkedPosts = communityService.getBookmarkedPosts(member.getMemberId());
        List<Map<String, Object>> bookmarkedPlaces = placeBookmarkService.getMemberPlaceBookmarks(member.getMemberId());
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
                    map.put("challengeId", t.getChallengeId());
                    map.put("name", t.getName());
                    map.put("minCount", t.getMinCount());
                    map.put("icon",t.getIcon());
                    achievedTitles.add(map);
            }


       // Map<String, Object> equippedTitle = achievedTitles.isEmpty() ? null : achievedTitles.get(0);
        Optional<Member> withEquippedTitleAndChallenge = memberRepository.findWithEquippedTitleAndChallenge(member.getMemberId());

        AchievedTitleDTO equippedTitleDto = withEquippedTitleAndChallenge
            .map(Member::getEquippedTitle)
            .filter(Objects::nonNull)
            .map(equippedTitle -> new AchievedTitleDTO(
                equippedTitle.getChallenge().getChallengeId(),
                equippedTitle.getName(),
                equippedTitle.getMinCount(),
                equippedTitle.getIcon()
            ))
            .orElse(null);

        MypageResponse.Data data = new MypageResponse.Data(
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
        MypageResponse response = new MypageResponse(data);
        return ResponseEntity.ok(ApiResponse.success(response));
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

    // ===== 내부 DTO 클래스들 =====
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SignupRequest {
        @Schema(description = "이메일", example = "test@test.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
        
        @Schema(description = "비밀번호", example = "password123!")
        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;
        
        @Schema(description = "비밀번호 확인", example = "password123!")
        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        private String passwordCheck;
        
        @Schema(description = "이름", example = "홍길동")
        @NotBlank(message = "이름은 필수입니다.")
        private String name;
        
        @Schema(description = "닉네임", example = "길동이")
        @NotBlank(message = "닉네임은 필수입니다.")
        private String nickname;
        
        @Schema(description = "휴대폰번호", example = "01012345678")
        @NotBlank(message = "휴대폰번호는 필수입니다.")
        @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰번호 형식이 올바르지 않습니다.")
        private String phoneNumber;
        
        @Schema(description = "초대코드 (선택사항)", example = "ABC123")
        private String inviteCode;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SignupResponse {
        private Long userId;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CheckEmailRequest {
        private String email;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CheckNicknameRequest {
        private String nickname;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CheckResponse {
        private boolean available;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @Schema(description = "이메일", example = "test3@test.com")
        private String email;
        @Schema(description = "비밀번호", example = "string")
        private String password;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginResponse {
        private Data data;
        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class Data {
            private String accessToken;
            private String refreshToken;
            private String grantType;
            private Long expiresIn;
            private Long refreshExpiresIn;
        }
    }

    // 아이디(이메일) 찾기 관련 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class FindEmailRequest {
        @Schema(description = "이름", example = "안재호")
        @NotBlank(message = "이름은 필수입니다.")
        private String name;
        @Schema(description = "휴대폰번호", example = "01012345678")
        @NotBlank(message = "휴대폰번호는 필수입니다.")
        @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰번호 형식이 올바르지 않습니다.")
        private String phoneNumber;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class FindEmailResponse {
        @Schema(description = "마스킹된 이메일 목록", example = "[\"te****@test.com\", \"an****@test.com\"]")
        private java.util.List<String> emails;
    }

    // 비밀번호 찾기 관련 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PasswordFindRequest {
        @Schema(description = "이메일", example = "test@test.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
    }

    @Getter @Setter @NoArgsConstructor
    public static class PasswordFindResponse {
        private String message = "임시 비밀번호가 이메일로 발송되었습니다.";
    }

    // 비밀번호 변경 관련 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PasswordChangeRequest {
        @Schema(description = "기존 비밀번호", example = "1234")
        @NotBlank(message = "기존 비밀번호는 필수입니다.")
        private String oldPassword;
        @Schema(description = "새 비밀번호", example = "5678")
        @NotBlank(message = "새 비밀번호는 필수입니다.")
        private String newPassword;
    }

    @Getter @Setter @NoArgsConstructor
    public static class PasswordChangeResponse {
        // 응답 데이터 없음 (성공 메시지만 반환)
    }

    // 로그아웃 관련 DTO
    @Getter @Setter
    public static class LogoutResponse {
        private String message = "로그아웃 성공";
        public LogoutResponse() {}
        public LogoutResponse(String message) { this.message = message; }
    }

    // 장소 북마크 해제 요청 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UnbookmarkRequest {
        @Schema(description = "장소 타입", example = "store|festival|library")
        private String placeType;
    }

    // 장소 북마크 해제 응답 DTO
    @Getter @Setter @NoArgsConstructor
    public static class UnbookmarkResponse {
        // 응답 데이터 없음 (성공 메시지만 반환)
    }

    // 마이페이지 조회 응답 DTO
    @Getter @Setter @NoArgsConstructor
    public static class MypageResponse {
        private Data data;
        
        public MypageResponse(Data data) {
            this.data = data;
        }
        
        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class Data {
            private Long memberId;
            private String email;
            private String name;
            private String nickname;
            private String profileImage;
            private int level;
            private int currentExp;
            private int nextLevelExp;
            private int expProgress;
            private List<Map<String, Object>> myPosts;
            private String goalStuff;
            private BigDecimal remainPrice;
            private List<Map<String, Object>> bookmarkedPosts;
            private List<Map<String, Object>> bookmarkedPlaces;
            private AchievedTitleDTO equippedTitle;
            private List<Map<String, Object>> achievedTitles;
        }
    }

    // 목표 설정 요청 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class GoalRequest {
        @Schema(description = "목표 금액", example = "200000")
        private BigDecimal goalAmount;
        
        @Schema(description = "목표 항목", example = "자동차")
        private String goalStuff;
    }

    // 목표 설정/조회 응답 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class GoalResponse {
        private BigDecimal goalAmount;
        private String goalStuff;
    }

    // 대표 칭호 변경 요청 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class RepresentativeTitleRequest {
        @Schema(description = "칭호 ID", example = "10001")
        @NotNull(message = "칭호 ID는 필수입니다.")
        private Long titleId;
    }

    // 대표 칭호 변경 응답 DTO
    @Getter @Setter @NoArgsConstructor
    public static class RepresentativeTitleResponse {
        private Long titleId;
        
        public RepresentativeTitleResponse(Long titleId) {
            this.titleId = titleId;
        }
    }

    // 칭호 즉시 장착 응답 DTO
    @Getter @Setter @NoArgsConstructor
    public static class EquipTitleResponse {
        private String equippedTitle;
        private List<String> achievedTitles;
        
        public EquipTitleResponse(String equippedTitle, List<String> achievedTitles) {
            this.equippedTitle = equippedTitle;
            this.achievedTitles = achievedTitles;
        }
    }

    // 칭호 목록 조회 응답 DTO
    @Getter @Setter @NoArgsConstructor
    public static class TitleResponse {
        private Long titleId;
        private String name;
        private Boolean achieved;
        private Integer minCount;
        private Long challengeId;
        private String challengeName;
        
        public TitleResponse(Long titleId, String name, Boolean achieved, Integer minCount, Long challengeId, String challengeName) {
            this.titleId = titleId;
            this.name = name;
            this.achieved = achieved;
            this.minCount = minCount;
            this.challengeId = challengeId;
            this.challengeName = challengeName;
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ProfileUpdateRequest {
        
        @Schema(description = "닉네임", example = "새로운닉네임")
        private String nickname;
        
        @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.jpg")
        private String profileImage;
        
        @Schema(description = "기존 비밀번호", example = "1234")
        private String oldPassword; // 기존 비밀번호(비밀번호 변경 시 필요)
        
        @Schema(description = "새 비밀번호", example = "newpassword123!")
        private String newPassword;
        
        @Schema(description = "새 비밀번호 확인", example = "newpassword123!")
        private String newPasswordCheck;
    }

    // 초대 코드 관련 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class InviteCodeResponse {
        @Schema(description = "초대 코드", example = "ABC123")
        private String inviteCode;
    }

    // 회원 탈퇴 관련 DTO
    @Getter @Setter @NoArgsConstructor
    public static class WithdrawResponse {
        // 응답 데이터 없음 (성공 메시지만 반환)
    }

    // 챌린지 대시보드 관련 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ChallengeDashboardResponse {
        @Schema(description = "챌린지 목록")
        private List<ChallengeData> challenges;
        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class ChallengeData {
            @Schema(description = "챌린지 ID", example = "1")
            private Long challengeId;
            @Schema(description = "챌린지 이름", example = "만원의 행복")
            private String name;
            @Schema(description = "챌린지 타입", example = "일일")
            private String type;
            @Schema(description = "챌린지 설명", example = "만원으로 하루 살아보기")
            private String description;
            @Schema(description = "총 목표", example = "1")
            private Integer total;
            @Schema(description = "진행률", example = "0")
            private Integer progress;
            @Schema(description = "아이콘", example = "moneyIcon")
            private String icon;
        }
    }
} 