package com.grepp.spring.app.controller.api.batchrun;

import com.grepp.spring.infra.publicdata.batch.Service.GeocodingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
@Profile("!mock")
public class GeocodingController {

    private final GeocodingService geocodingService;

    @PostMapping("/stores/update")
    @Operation(summary = "좌표값 받아오기.")
    public ResponseEntity<String> updateStoreCoordinates() throws InterruptedException {
        System.out.println("컨트롤러 입장.");
        geocodingService.updateAllStoreCoords();
        return ResponseEntity.ok("store 주소 -> 좌표 변환 완료!");
    }
}
