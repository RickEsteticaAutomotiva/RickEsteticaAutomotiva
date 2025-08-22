package com.automotiva.estetica.rick.api_agendamento_servicos.controller;


import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CalendarEventRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("calendar/events")
public class GoogleCalendarController {
    @Autowired
    private GoogleCalendarService calendarService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return calendarService.isServiceAvailable()
                ? ResponseEntity.ok("Google Calendar Service is available")
                : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Google Calendar Service is not available");
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody CalendarEventRequest request) {
        if (!calendarService.isServiceAvailable()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Google Calendar Service is not available");
        }

        try {
            Event event = calendarService.createEvent(request);
            return ResponseEntity.ok(event);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Error creating event: " + e.getMessage());
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable String eventId) {
        if (!calendarService.isServiceAvailable()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Google Calendar Service is not available");
        }

        try {
            Event event = calendarService.getEvent(eventId);
            return ResponseEntity.ok(event);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<?> listEvents() {
        if (!calendarService.isServiceAvailable()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Google Calendar Service is not available");
        }

        try {
            List<Event> events = calendarService.listEvents();
            return ResponseEntity.ok(events);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Error listing events: " + e.getMessage());
        }
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(
            @PathVariable String eventId,
            @RequestBody CalendarEventRequest request) {
        if (!calendarService.isServiceAvailable()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Google Calendar Service is not available");
        }

        try {
            Event event = calendarService.updateEvent(eventId, request);
            return ResponseEntity.ok(event);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Error updating event: " + e.getMessage());
        }
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable String eventId) {
        if (!calendarService.isServiceAvailable()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Google Calendar Service is not available");
        }

        try {
            calendarService.deleteEvent(eventId);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Error deleting event: " + e.getMessage());
        }
    }
}
