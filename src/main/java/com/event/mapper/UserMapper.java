package com.event.mapper;

import com.event.dto.UserDto;
import com.event.entity.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        if(user == null) {
            return null;
        }

        return new UserDto(
                user.getJmbg(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                "SECRET",
                user.getEmail(),
                user.getRole(),
                user.isActive()
        );
    }

    public static User toUser(UserDto userDto) {
        if(userDto == null) {
            return null;
        }

        return new User(
                userDto.getJmbg(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getUsername(),
                userDto.getPassword(),
                userDto.getEmail(),
                userDto.getRole(),
                userDto.isActive()
        );
    }
}
