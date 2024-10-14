package com.event.service;

import com.event.dto.DirectionDto;
import com.event.dto.EventDto;
import com.event.dto.EventOrderField;
import com.event.dto.EventSearchModel;
import com.event.entity.Event;
import com.event.entity.QEvent;
import com.event.entity.User;
import com.event.exception.BadRequestException;
import com.event.exception.ExceptionCode;
import com.event.mapper.EventMapper;
import com.event.repository.EventRepository;
import com.event.repository.UserRepository;
import com.event.validator.security.SecurityService;
import com.event.validator.EventValidator;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventValidator eventValidator;
    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(EventService.class);
    @Transactional
    public EventDto createEvent(EventDto eventDto) {
        logger.info("Started creating new event");
        eventValidator.validateCreateEventRequest(eventDto);
        Event event = EventMapper.toEvent(eventDto);
        eventRepository.save(event);
        logger.info("Created event with name: {}", eventDto.getName());
        return EventMapper.toEventDto(event);
    }

    @Transactional(readOnly = true)
    public List<EventDto> searchEvents(EventSearchModel eventSearchModel) {
        logger.info("Starting search event with parameters {}", eventSearchModel);
        QEvent qEvent = QEvent.event;
        BooleanBuilder booleanBuilder = prepareBooleanBuilder(eventSearchModel, qEvent);
        Iterable<Event> eventIterableList = eventRepository.findAll(booleanBuilder, preparePageRequest(eventSearchModel));
        List<EventDto> eventDtoList = new ArrayList<>();
        eventIterableList.forEach(event -> eventDtoList.add(EventMapper.toEventDto(event)));
        return eventDtoList;
    }

    private PageRequest preparePageRequest(EventSearchModel eventSearchModel) {
        int pageNumber = eventSearchModel.getPageNumber() == null ? 0 : eventSearchModel.getPageNumber();
        int pageSize = eventSearchModel.getPageSize() == null ? 10 : eventSearchModel.getPageSize();
        String direction = eventSearchModel.getDirection() == null ? DirectionDto.ASC.getDirection() : eventSearchModel.getDirection().getDirection();
        String orderField = eventSearchModel.getSortField() == null ? EventOrderField.START_TIME.getLabel() : eventSearchModel.getSortField().getLabel();
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(direction), orderField));
    }

    private BooleanBuilder prepareBooleanBuilder(EventSearchModel eventSearchModel, QEvent qEvent) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if(eventSearchModel.getId() != null) {
            booleanBuilder.and(qEvent.id.eq(eventSearchModel.getId()));
        }
        if(StringUtils.isNotBlank(eventSearchModel.getName())) {
            booleanBuilder.and(qEvent.name.containsIgnoreCase(eventSearchModel.getName()));
        }
        if(StringUtils.isNotBlank(eventSearchModel.getLocation())) {
            booleanBuilder.and(qEvent.location.containsIgnoreCase(eventSearchModel.getLocation()));
        }
        if(StringUtils.isNotBlank(eventSearchModel.getDescription())) {
            booleanBuilder.and(qEvent.description.containsIgnoreCase(eventSearchModel.getDescription()));
        }
        if(eventSearchModel.getStartTimeFrom() != null) {
            booleanBuilder.and(qEvent.startTime.goe(eventSearchModel.getStartTimeFrom()));
        }
        if(eventSearchModel.getStartTimeTo() != null) {
            booleanBuilder.and(qEvent.startTime.loe(eventSearchModel.getStartTimeTo()));
        }
        if(eventSearchModel.getEndTimeFrom() != null) {
            booleanBuilder.and(qEvent.endTime.goe(eventSearchModel.getEndTimeFrom()));
        }
        if(eventSearchModel.getEndTimeTo() != null) {
            booleanBuilder.and(qEvent.endTime.loe(eventSearchModel.getEndTimeTo()));
        }
        return booleanBuilder;
    }

    @Transactional
    public void deleteEventById(Long id) {
        logger.info("Started deleting event with id {}", id);
        eventValidator.validateEventExistence(id);
        eventRepository.deleteById(id);
    }

    @Transactional
    public void subscribeToEvent(Long eventId) {
        logger.info("Subscribe user with username {} to event with id {}", securityService.getUsernameFromContext(), eventId);
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if(eventOptional.isEmpty()) {
            throw new BadRequestException(ExceptionCode.NOT_EXISTS.name(), String.format("Event with id %s does not exists", eventId));
        }
        String username = securityService.getUsernameFromContext();
        User user = userRepository.findByUsername(username);
        Event event = eventOptional.get();
        if(event.getSubscribedUserList().contains(user)){
            throw new BadRequestException(ExceptionCode.ALREADY_EXISTS.name(), "You are already subscribed for this event");
        }
        event.getSubscribedUserList().add(user);
        eventRepository.save(event);
    }

}
