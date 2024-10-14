package com.event.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PageRequestDto<T> {
    private Integer pageNumber = 0;
    private Integer pageSize = 10;
    private T sortField;
    private DirectionDto direction = DirectionDto.ASC;
}
