package com.grepp.spring.app.controller.api.batchrun;

import com.grepp.spring.infra.publicdata.batch.Service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    @PostMapping("/stores/update")
    public ResponseEntity<String> updateStoreCoordinates() throws InterruptedException {
        System.out.println("컨트롤러 입장.");
        geocodingService.updateAllStoreCoords();
        return ResponseEntity.ok("store 주소 -> 좌표 변환 완료!");
    }
}
