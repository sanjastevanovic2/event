package com.event.mapper;

import com.event.dto.EventDto;
import com.event.entity.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static Event toEvent(EventDto eventDto) {
        if(eventDto == null) {
            return null;
        }

        return new Event(
                eventDto.getId(),
                eventDto.getName(),
                eventDto.getDescription(),
                eventDto.getLocation(),
                eventDto.getStartTime(),
                eventDto.getEndTime(),
                Collections.emptySet()
        );
    }

    public static EventDto toEventDto(Event event) {
        if(event == null) {
            return null;
        }
        return new EventDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime()
        );
    }

    public static List<EventDto> toEventDtoList(List<Event> eventList) {
        if(CollectionUtils.isEmpty(eventList)) {
            return Collections.emptyList();
        }

        List<EventDto> eventDtoList = new ArrayList<>();
        for(Event event : eventList) {
            eventDtoList.add(toEventDto(event));
        }

        return eventDtoList;
    }
}
