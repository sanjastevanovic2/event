package com.event.controller;

import com.event.dto.EventDto;
import com.event.dto.EventSearchModel;
import com.event.dto.UserDto;
import com.event.dto.UserSearchModel;
import com.event.exception.BadRequestException;
import com.event.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "API for creating user", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema =
            @Schema(implementation = UserDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema =
            @Schema(implementation = BadRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Parameter(description = "User Dto for creating new user", schema = @Schema(implementation = UserDto.class))
            @RequestBody UserDto userDto
    ) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.OK);
    }

    @Operation(summary = "API for updating user", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema =
            @Schema(implementation = UserDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema =
            @Schema(implementation = BadRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PutMapping
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "User Dto for updating existing user", schema = @Schema(implementation = UserDto.class))
            @RequestBody UserDto userDto
    ) {
        return new ResponseEntity<>(userService.updateUser(userDto), HttpStatus.OK);
    }


    @Operation(summary = "API for updating user active status", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema =
            @Schema(implementation = String.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema =
            @Schema(implementation = BadRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PutMapping("/active-status/{jmbg}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateActiveStatus(
            @Parameter(description = "User jmbg", schema = @Schema(implementation = String.class))
            @PathVariable String jmbg,
            @Parameter(description = "User status", schema = @Schema(implementation = boolean.class))
            @RequestParam boolean active) {
        userService.changeActiveStatus(jmbg, active);
        return new ResponseEntity<>("Successfully updated user status", HttpStatus.OK);
    }

    @Operation(summary = "API for searching users", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(array = @ArraySchema(schema =
            @Schema(implementation = UserDto.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema =
            @Schema(implementation = BadRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDto>> searchUserList(
            @Parameter(description = "User search model", schema = @Schema(implementation = UserSearchModel.class))
            UserSearchModel userSearchModel
    ) {
        return new ResponseEntity<>(userService.searchUsers(userSearchModel), HttpStatus.OK);
    }

    @Operation(summary = "API for deleting user", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema =
            @Schema(implementation = String.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema =
            @Schema(implementation = BadRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @DeleteMapping("/{jmbg}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "User jmbg", schema = @Schema(implementation = String.class))
            @PathVariable String jmbg
    ) {
        userService.deleteUserByJmbg(jmbg);
        return new ResponseEntity<>("User is successfully deleted", HttpStatus.OK);
    }

}
