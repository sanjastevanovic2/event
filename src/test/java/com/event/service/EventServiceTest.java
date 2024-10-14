package com.event.service;

import com.event.dto.EventDto;
import com.event.dto.EventSearchModel;
import com.event.entity.Event;
import com.event.entity.User;
import com.event.mapper.EventMapper;
import com.event.repository.EventRepository;
import com.event.repository.UserRepository;
import com.event.util.EventUtil;
import com.event.validator.EventValidator;
import com.event.validator.security.SecurityService;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    EventValidator eventValidator;

    @Mock
    SecurityService securityService;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    EventService eventService;

    @Test
    void createEvent() {
        EventDto eventDto = new EventDto();
        eventDto.setName("Event");
        Event event = new Event();
        doNothing().when(eventValidator).validateCreateEventRequest(any());
        doReturn(event).when(eventRepository).save(any());
        EventDto result = eventService.createEvent(eventDto);
        assertNotNull(result);
        assertEquals("Event", result.getName());
        verify(eventValidator).validateCreateEventRequest(eventDto);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void searchEvents() {
        EventSearchModel searchModel = new EventSearchModel();
        searchModel.setPageNumber(0);
        searchModel.setPageSize(10);
        List<Event> events = Arrays.asList(new Event(), new Event());
        Page<Event> eventPage = new PageImpl<>(events);
        doReturn(eventPage).when(eventRepository).findAll(any(BooleanBuilder.class), any(PageRequest.class));
        List<EventDto> result = eventService.searchEvents(searchModel);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventRepository).findAll(any(BooleanBuilder.class), any(PageRequest.class));
    }

    @Test
    void deleteEventById() {
        Long eventId = 1L;
        doNothing().when(eventValidator).validateEventExistence(eventId);
        doNothing().when(eventRepository).deleteById(eventId);
        eventService.deleteEventById(eventId);
        verify(eventValidator).validateEventExistence(eventId);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void subscribeToEvent() {

        Long eventId = 1L;
        String username = "testuser";
        Event event = new Event();
        User user = new User();
        event.setSubscribedUserList(new HashSet<>());

        when(securityService.getUsernameFromContext()).thenReturn(username);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findByUsername(username)).thenReturn(user);

        eventService.subscribeToEvent(eventId);

        assertTrue(event.getSubscribedUserList().contains(user));
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(event);
    }
}