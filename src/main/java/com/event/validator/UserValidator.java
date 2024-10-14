package com.event.validator;

import com.event.dto.Role;
import com.event.dto.UserDto;
import com.event.entity.User;
import com.event.exception.BadRequestException;
import com.event.exception.ExceptionCode;
import com.event.repository.UserRepository;
import com.event.validator.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserValidator {

    private final Logger logger = LoggerFactory.getLogger(UserValidator.class);
    private final ObjectValidator objectValidator;
    private final StringValidator stringValidator;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public void validateUserCreateRequest(UserDto userDto) {
        objectValidator.validateObjectNotNull(userDto, "userDto");
        stringValidator.validateStringNotBlank(userDto.getJmbg(), "jmbg");
        stringValidator.validateStringNotBlank(userDto.getUsername(), "username");
        stringValidator.validateStringNotBlank(userDto.getPassword(), "password");

        User user = userRepository.findByJmbgOrUsername(userDto.getJmbg(), userDto.getUsername());
        if(user != null) {
            if(StringUtils.equals(user.getJmbg(), userDto.getJmbg())) {
                logger.error("User with jmbg {} already exists", userDto.getJmbg());
                throw new BadRequestException(ExceptionCode.ALREADY_EXISTS.name(), String.format("User with jmbg %s already exists", userDto.getJmbg()));
            }
            if(StringUtils.equals(user.getUsername(), userDto.getUsername())) {
                logger.error("User with username {} already exists", userDto.getUsername());
                throw new BadRequestException(ExceptionCode.ALREADY_EXISTS.name(), String.format("User with username %s already exists", userDto.getUsername()));
            }
        }
    }

    @Transactional(readOnly = true)
    public void validateUserUpdateRequest(UserDto userDto) {
        objectValidator.validateObjectNotNull(userDto, "userDto");
        stringValidator.validateStringNotBlank(userDto.getJmbg(), "jmbg");
        Optional<User> userOptional = userRepository.findById(userDto.getJmbg());
        if(userOptional.isEmpty()) {
            logger.error("User with jmbg {} does not exist", userDto.getJmbg());
            throw new BadRequestException(ExceptionCode.NOT_EXISTS.name(), String.format("User with jmbg %s does not exist", userDto.getJmbg()));
        }

        User user = userOptional.get();
        User loggedUser = userRepository.findByUsername(securityService.getUsernameFromContext());
        if(Role.REGULAR_USER.equals(loggedUser.getRole()) && !StringUtils.equals(user.getUsername(), securityService.getUsernameFromContext())) {
            logger.error("User {} have role regular user, and cannot update someone else profile", securityService.getUsernameFromContext());
            throw new BadRequestException(ExceptionCode.NOT_ALLOWED.name(), "As regular user you can edit only your profile");
        }

        if(userRepository.existsByUsernameAndDifferentJmbg(userDto.getUsername(), userDto.getJmbg())) {
            logger.error("Username {} already exist for other user", userDto.getUsername());
            throw new BadRequestException(ExceptionCode.ALREADY_EXISTS.name(), "Username that you try to set is already used by some other user");
        }

    }

    @Transactional(readOnly = true)
    public void validateExistenceByJmbg(String jmbg) {
        if(!userRepository.existsById(jmbg)) {
            logger.error("User with jmbg {} does not exist", jmbg);
            throw new BadRequestException(ExceptionCode.NOT_EXISTS.name(), String.format("User with jmbg %s does not exist", jmbg));
        }
    }

    @Transactional(readOnly = true)
    public void validateUserDeletion(String jmbg) {
        stringValidator.validateStringNotBlank(jmbg, "jmbg");
        validateExistenceByJmbg(jmbg);
    }
}
