package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.store.model.StoreDTO;
import com.grepp.spring.app.model.store.service.StoreService;
import com.grepp.spring.util.ReferencedException;
import com.grepp.spring.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/stores", produces = MediaType.APPLICATION_JSON_VALUE)
public class StoreResource {

    private final StoreService storeService;

    public StoreResource(final StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        return ResponseEntity.ok(storeService.findAll());
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDTO> getStore(@PathVariable(name = "storeId") final Long storeId) {
        return ResponseEntity.ok(storeService.get(storeId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createStore(@RequestBody @Valid final StoreDTO storeDTO) {
        final Long createdStoreId = storeService.create(storeDTO);
        return new ResponseEntity<>(createdStoreId, HttpStatus.CREATED);
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<Long> updateStore(@PathVariable(name = "storeId") final Long storeId,
            @RequestBody @Valid final StoreDTO storeDTO) {
        storeService.update(storeId, storeDTO);
        return ResponseEntity.ok(storeId);
    }

    @DeleteMapping("/{storeId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteStore(@PathVariable(name = "storeId") final Long storeId) {
        final ReferencedWarning referencedWarning = storeService.getReferencedWarning(storeId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        storeService.delete(storeId);
        return ResponseEntity.noContent().build();
    }

}
