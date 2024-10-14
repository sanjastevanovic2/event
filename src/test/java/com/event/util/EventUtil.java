package com.event.util;

import com.event.dto.EventDto;
import com.event.entity.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventUtil {

    public static Event getEvent() {
        return new Event(1l, "test", "test", "test", LocalDateTime.now(), LocalDateTime.now().plusDays(2), Collections.emptySet());
    }

    public static EventDto getEventDto() {
        return new EventDto(1l, "test", "test", "test", LocalDateTime.now(), LocalDateTime.now().plusDays(2));
    }
}
