package com.event.service;

import com.event.dto.*;
import com.event.entity.QUser;
import com.event.entity.User;
import com.event.mapper.UserMapper;
import com.event.repository.UserRepository;
import com.event.validator.UserValidator;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        logger.info("Started creating user");
        userValidator.validateUserCreateRequest(userDto);
        User user = UserMapper.toUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.REGULAR_USER);
        userRepository.save(user);
        logger.info("User with jmbg {} created", userDto.getJmbg());
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto updateUser(UserDto userDto) {
        logger.info("Updating user started");
        userValidator.validateUserUpdateRequest(userDto);
        User user = userRepository.findById(userDto.getJmbg()).get();
        prapareUserForUpdate(userDto, user);
        userRepository.save(user);
        logger.info("Updated user with jmbg: {}", userDto.getJmbg());
        return UserMapper.toUserDto(user);
    }

    private void prapareUserForUpdate(UserDto userDto, User user) {
        if(StringUtils.isNotBlank(userDto.getFirstName())) {
            user.setFirstName(userDto.getFirstName());
        }
        if(StringUtils.isNotBlank(userDto.getLastName())) {
            user.setLastName(userDto.getLastName());
        }
        if(StringUtils.isNotBlank(userDto.getUsername())) {
            user.setLastName(userDto.getLastName());
        }
        if(StringUtils.isNotBlank(userDto.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        if(StringUtils.isNotBlank(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
        if(userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }
    }

    @Transactional
    public void changeActiveStatus(String jmbg, boolean active) {
        userValidator.validateExistenceByJmbg(jmbg);
        userRepository.changeActiveStatus(jmbg, active);
    }

    @Transactional(readOnly = true)
    public List<UserDto> searchUsers(UserSearchModel userSearchModel) {
        logger.info("Started searching users with parameters: {}", userSearchModel);
        BooleanBuilder booleanBuilder = prepareBooleanBuilder(userSearchModel);
        String orderField = userSearchModel.getSortField() == null ? UserOrderField.JMBG.getLabel() : userSearchModel.getSortField().getLabel();
        Iterable<User> userIterable = userRepository.findAll(booleanBuilder, PageRequest.of(userSearchModel.getPageNumber(), userSearchModel.getPageSize(), Sort.by(Sort.Direction.fromString(userSearchModel.getDirection().getDirection()), orderField)));
        List<UserDto> userDtoList = new ArrayList<>();
        userIterable.forEach(user -> userDtoList.add(UserMapper.toUserDto(user)));
        return userDtoList;
    }

    private BooleanBuilder prepareBooleanBuilder(UserSearchModel userSearchModel) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QUser qUser = QUser.user;
        if(StringUtils.isNotBlank(userSearchModel.getJmbg())) {
            booleanBuilder.and(qUser.jmbg.containsIgnoreCase(userSearchModel.getJmbg()));
        }
        if(StringUtils.isNotBlank(userSearchModel.getFirstName())) {
            booleanBuilder.and(qUser.firstName.containsIgnoreCase(userSearchModel.getFirstName()));
        }
        if(StringUtils.isNotBlank(userSearchModel.getLastName())) {
            booleanBuilder.and(qUser.lastName.containsIgnoreCase(userSearchModel.getLastName()));
        }
        if(StringUtils.isNotBlank(userSearchModel.getUsername())) {
            booleanBuilder.and(qUser.username.containsIgnoreCase(userSearchModel.getUsername()));
        }
        if(StringUtils.isNotBlank(userSearchModel.getEmail())) {
            booleanBuilder.and(qUser.email.containsIgnoreCase(userSearchModel.getEmail()));
        }
        if(userSearchModel.getRole() != null) {
            booleanBuilder.and(qUser.role.eq(userSearchModel.getRole()));
        }
        if(userSearchModel.getActive() != null) {
            booleanBuilder.and(qUser.active.eq(userSearchModel.getActive()));
        }
        return booleanBuilder;
    }

    @Transactional
    public void deleteUserByJmbg(String jmbg) {
        userValidator.validateUserDeletion(jmbg);
        userRepository.deleteById(jmbg);
    }
}
