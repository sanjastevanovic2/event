package com.event.validator;

import com.event.dto.EventDto;
import com.event.exception.BadRequestException;
import com.event.exception.ExceptionCode;
import com.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventValidator {

    private final ObjectValidator objectValidator;
    private final StringValidator stringValidator;
    private final EventRepository eventRepository;
    private final Logger logger = LoggerFactory.getLogger(EventValidator.class);

    @Transactional(readOnly = true)
    public void validateCreateEventRequest(EventDto eventDto) {
        logger.info("Started validation for create event");
        objectValidator.validateObjectNotNull(eventDto, "EventDto");
        stringValidator.validateStringNotBlank(eventDto.getName(), "name");
        stringValidator.validateStringNotBlank(eventDto.getLocation(), "location");
        objectValidator.validateObjectNotNull(eventDto.getStartTime(), "startTime");
        objectValidator.validateObjectNotNull(eventDto.getEndTime(), "endTime");
        if(eventDto.getEndTime().isBefore(eventDto.getStartTime())) {
            logger.error("End time: {} cannot be before start time: {}", eventDto.getEndTime(), eventDto.getStartTime());
            throw new BadRequestException(ExceptionCode.NOT_ALLOWED.name(), "End time cannot be before start time");
        }
        if(eventRepository.existsByName(eventDto.getName())) {
            logger.error("Event with name {} already exists", eventDto.getName());
            throw new BadRequestException(ExceptionCode.ALREADY_EXISTS.name(), String.format("Event with name %s already exists", eventDto.getName()));
        }
        if(eventRepository.existsAtSameLocationWithTimeOverlap(eventDto.getLocation(), eventDto.getStartTime(), eventDto.getEndTime())) {
            logger.error("Event at location {} already exists for provided period {} - {}", eventDto.getLocation(), eventDto.getStartTime(), eventDto.getEndTime());
            throw new BadRequestException(ExceptionCode.ALREADY_EXISTS.name(), "Already exists event at the same location for provided period");
        }
    }

    @Transactional(readOnly = true)
    public void validateEventExistence(Long id) {
        objectValidator.validateObjectNotNull(id, "id");
        if(!eventRepository.existsById(id)) {
            logger.error("Event with id {} does not exist", id);
            throw new BadRequestException(ExceptionCode.NOT_EXISTS.name(), String.format("Event with id %s does not exist", id));
        }
    }
}
