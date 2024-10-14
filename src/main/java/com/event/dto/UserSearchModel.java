package com.event.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserSearchModel extends PageRequestDto<UserOrderField> {

    private String jmbg;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Role role;
    private Boolean active;
}
