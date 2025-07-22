package com.grepp.spring.app.model.admin.dto;

import java.util.List;

public record AdminUserListResponse(
    Integer total,
    List<AdminUserResponse> users
) {

}
