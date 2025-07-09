package com.grepp.spring.app.controller.api.admin;

import com.grepp.spring.app.model.admin.dto.AdminUserResponse;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/admin-users", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUsersController {

    List<AdminUserResponse> mockUsers = List.of(
        new AdminUserResponse(0, "거지왕", "abc@abc.com", false),
        new AdminUserResponse(1, "부자왕", "def@def.com", false)
    );

    @GetMapping
    @Operation(summary = "관리자 모든 회원 조회")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers() {
        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(mockUsers));
    }


    @GetMapping("/search")
    @Operation(summary = "관리자 유저 닉네임으로 유저 검색")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> searchByNickname(@RequestParam String nickname) {
        List<AdminUserResponse> result = mockUsers.stream()
            .filter(user -> user.nickname().equals(nickname))
            .toList();

        if (result.isEmpty()) {
            return ResponseEntity
                .status(ResponseCode.NOT_FOUND.status())
                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(result));
    }

    @PatchMapping("/{user-id}/block")
    @Operation(summary = "관리자 유저 차단")
    public ResponseEntity<ApiResponse<Map<String, String>>> blockUser(@PathVariable("user-id") int id) {

        if(id != 1 && id != 2){
            return ResponseEntity
                .status(ResponseCode.NOT_FOUND.status())
                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "유저가 차단되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

}
