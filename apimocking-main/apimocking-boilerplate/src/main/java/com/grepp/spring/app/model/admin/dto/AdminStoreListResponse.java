package com.grepp.spring.app.model.admin.dto;

import java.util.List;

public record AdminStoreListResponse(
    Integer total,
    List<AdminStoreResponse> stores
) {

}
