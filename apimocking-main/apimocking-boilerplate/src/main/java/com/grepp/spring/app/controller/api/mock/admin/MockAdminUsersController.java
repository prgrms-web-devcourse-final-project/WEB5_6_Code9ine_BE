package com.grepp.spring.app.controller.api.mock.admin;

import com.grepp.spring.app.model.admin.dto.AdminUserResponse;
import com.grepp.spring.infra.payload.PageParam;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("mock")
@RequestMapping(value = "/api/admin-users", produces = MediaType.APPLICATION_JSON_VALUE)
public class MockAdminUsersController {

    List<AdminUserResponse> mockUsers = List.of(
        new AdminUserResponse(0, "닉네임0", "abc@abc.com", false),
        new AdminUserResponse(1, "닉네임1", "def@def.com", false),
        new AdminUserResponse(2, "닉네임2", "ghi@ghi.com", false),
        new AdminUserResponse(3, "닉네임3", "jkl@jkl.com", false),
        new AdminUserResponse(4, "닉네임4", "mno@mno.com", false),
        new AdminUserResponse(5, "닉네임5", "pqr@pqr.com", false),
        new AdminUserResponse(6, "닉네임6", "stu@stu.com", false),
        new AdminUserResponse(7, "닉네임7", "wxyz@wxyz.com", false),
        new AdminUserResponse(8, "닉네임8", "aaa@aaa.com", false),
        new AdminUserResponse(9, "닉네임9", "bbb@bbb.com", false),
        new AdminUserResponse(10, "닉네임10", "ccc@ccc.com", false),
        new AdminUserResponse(11, "닉네임11", "ddd@ddd.com", false)
        );

    @GetMapping
    @Operation(summary = "관리자 모든 회원 조회")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers(
        @ParameterObject PageParam pageParam
    ) {
        int page = pageParam.getPage();
        int size = pageParam.getSize();

        int fromIndex =  (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, mockUsers.size());

        if (fromIndex >= mockUsers.size()) {
            return ResponseEntity
                .status(ResponseCode.BAD_REQUEST.status())
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }

        List<AdminUserResponse> paged = mockUsers.subList(fromIndex, toIndex);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(paged));
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
