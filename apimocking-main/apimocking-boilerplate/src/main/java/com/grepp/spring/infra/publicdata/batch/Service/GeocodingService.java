package com.grepp.spring.infra.publicdata.batch.Service;

import com.grepp.spring.app.model.store.domain.Store;
import com.grepp.spring.app.model.store.repos.StoreRepository;
import com.grepp.spring.infra.publicdata.batch.apiclient.KakaoAddressClient;
import com.grepp.spring.infra.publicdata.batch.dto.CoordDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final StoreRepository storeRepository;
    private final KakaoAddressClient kakaoAddressClient;

    // 한 페이지 제한 행 수
    private static final int BATCH_SIZE = 100;

    // 주소값을 페이징 처리해 넘겨주기
    public void updateAllStoreCoords() {
        int page = 0;
        int success = 0;
        int fail = 0;

        Page<Store> storePage;

        do {
            Pageable pageable = PageRequest.of(page, BATCH_SIZE);
            storePage = storeRepository.findAll(pageable);
            log.info("=== {}번째 페이지 처리 시작 (총 {}건) ===", page + 1, storePage.getNumberOfElements());

            for (Store store : storePage) {
                if (store.getLatitude() != null && store.getLongitude() != null) {
                    continue;
                }

                String address = store.getAddress();
                if (address == null || address.isBlank()) {
                    log.info("주소 없음 - store: {}", store.getName());
                    continue;
                }

                try {
                    boolean updated = updateStoreCoord(store);
                    if (updated) {
                        success++;
                    } else {
                        fail++;
                    }
                } catch (Exception e) {
                    log.info("예외 발생 - 가게이름: {}, 주소: {}", store.getName(), store.getAddress(), e);
                    fail++;
                }

                sleep(); // API 부하 방지
            }

            page++;

        } while (storePage.hasNext());

        log.info("좌표 변환 완료 - 전체: {}, 성공: {}, 실패: {}", success + fail, success, fail);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean updateStoreCoord(Store store) {
        Optional<CoordDto> coordOpt = kakaoAddressClient.getCoordinates(store.getAddress());

        if (coordOpt.isPresent()) {
            CoordDto coord = coordOpt.get();
            store.setLatitude(coord.getLatitude());
            store.setLongitude(coord.getLongitude());
            storeRepository.save(store);

            log.info("[{}] 주소: {}, 위도: {}, 경도: {}", store.getName(), store.getAddress(), coord.getLatitude(), coord.getLongitude());
            return true;
        } else {
            log.info("[{}] 주소 좌표 변환 실패 - {}", store.getName(), store.getAddress());
            return false;
        }
    }

    private void sleep() {
        try {
            Thread.sleep(300); // 카카오 API 제한 고려
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}