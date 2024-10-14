package com.event.controller;

import com.event.dto.EventDto;
import com.event.dto.EventSearchModel;
import com.event.exception.BadRequestException;
import com.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@Tag(name = "Event APIs", description = "REST APIs for manipulation with events")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "API for creating event", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema =
            @Schema(implementation = EventDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema =
            @Schema(implementation = BadRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<EventDto> createEvent(
            @Parameter(description = "Event Dto for creating new event", schema = @Schema(implementation = EventDto.class))
            @RequestBody EventDto eventDto
    ) {
        return new ResponseEntity<>(eventService.createEvent(eventDto), HttpStatus.OK);
    }

    @Operation(summary = "API for searching events", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(array = @ArraySchema(schema =
            @Schema(implementation = EventDto.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema =
            @Schema(implementation = BadRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_REGULAR_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<EventDto>> searchEventList(
            @Parameter(description = "Event search model", schema = @Schema(implementation = EventSearchModel.class))
            EventSearchModel eventSearchModel) {
        return new ResponseEntity<>(eventService.searchEvents(eventSearchModel), HttpStatus.OK);
    }

    @Operation(summary = "API for deleting event", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema =
            @Schema(implementation = String.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema =
            @Schema(implementation = BadRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteEntityById(
            @Parameter(description = "Id of event", schema = @Schema(implementation = Long.class))
            @PathVariable Long id
    ) {
        eventService.deleteEventById(id);
        return new ResponseEntity<>(String.format("Entity with id %s is successfully deleted", id), HttpStatus.OK);
    }

    @Operation(summary = "API for subscribing to event", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema =
            @Schema(implementation = String.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema =
            @Schema(implementation = BadRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_REGULAR_USER')")
    @PutMapping("/subscribe/{eventId}")
    public ResponseEntity<String> subscribeToEvent(@PathVariable Long eventId) {
        eventService.subscribeToEvent(eventId);
        return new ResponseEntity<>("User successfully subscribed to event", HttpStatus.OK);
    }
}
