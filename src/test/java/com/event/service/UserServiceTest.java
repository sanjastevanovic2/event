package com.event.service;

import com.event.dto.*;
import com.event.entity.User;
import com.event.mapper.UserMapper;
import com.event.repository.UserRepository;
import com.event.validator.UserValidator;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserValidator userValidator;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void createUser() {
        UserDto userDto = new UserDto();
        userDto.setJmbg("123456789");
        userDto.setPassword("password");
        User user = new User();
        user.setJmbg("123456789");
        doNothing().when(userValidator).validateUserCreateRequest(any());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto result = userService.createUser(userDto);
        assertNotNull(result);
        assertEquals("123456789", result.getJmbg());
    }

    @Test
    void updateUser() {
        UserDto userDto = new UserDto();
        userDto.setJmbg("123456789");
        userDto.setFirstName("UpdatedName");
        User user = new User();
        user.setJmbg("123456789");

        doNothing().when(userValidator).validateUserUpdateRequest(any());
        when(userRepository.findById(userDto.getJmbg())).thenReturn(Optional.of(user));
        UserDto result = userService.updateUser(userDto);
        assertNotNull(result);
        assertEquals("UpdatedName", result.getFirstName());
    }

    @Test
    void changeActiveStatus() {
        String jmbg = "123456789";
        boolean activeStatus = true;
        doNothing().when(userValidator).validateExistenceByJmbg(jmbg);
        doNothing().when(userRepository).changeActiveStatus(jmbg, activeStatus);
        userService.changeActiveStatus(jmbg, activeStatus);
        verify(userValidator).validateExistenceByJmbg(jmbg);
        verify(userRepository).changeActiveStatus(jmbg, activeStatus);
    }

    @Test
    void searchUsers() {
        UserSearchModel searchModel = new UserSearchModel();
        searchModel.setPageNumber(0);
        searchModel.setPageSize(10);

        User user1 = new User();
        User user2 = new User();
        List<User> users = Arrays.asList(user1, user2);
        PageImpl<User> userPage = new PageImpl<>(users);

        doReturn(userPage).when(userRepository).findAll(any(BooleanBuilder.class), any(PageRequest.class));
        List<UserDto> result = userService.searchUsers(searchModel);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}