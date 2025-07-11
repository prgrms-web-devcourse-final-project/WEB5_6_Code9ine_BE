package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.notification.model.NotificationDTO;
import com.grepp.spring.app.model.notification.service.NotificationService;
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
import org.springframework.context.annotation.Profile;


@RestController
@Profile("!mock")
@RequestMapping(value = "/api/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationResource {

    private final NotificationService notificationService;

    public NotificationResource(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationDTO> getNotification(
            @PathVariable(name = "notificationId") final Long notificationId) {
        return ResponseEntity.ok(notificationService.get(notificationId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createNotification(
            @RequestBody @Valid final NotificationDTO notificationDTO) {
        final Long createdNotificationId = notificationService.create(notificationDTO);
        return new ResponseEntity<>(createdNotificationId, HttpStatus.CREATED);
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<Long> updateNotification(
            @PathVariable(name = "notificationId") final Long notificationId,
            @RequestBody @Valid final NotificationDTO notificationDTO) {
        notificationService.update(notificationId, notificationDTO);
        return ResponseEntity.ok(notificationId);
    }

    @DeleteMapping("/{notificationId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable(name = "notificationId") final Long notificationId) {
        notificationService.delete(notificationId);
        return ResponseEntity.noContent().build();
    }

}
