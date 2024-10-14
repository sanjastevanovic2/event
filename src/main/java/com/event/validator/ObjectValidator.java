package com.event.validator;

import com.event.exception.BadRequestException;
import com.event.exception.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ObjectValidator {

    private final Logger logger = LoggerFactory.getLogger(ObjectValidator.class);
    public void validateObjectNotNull(Object object, String name) {
        if(object == null) {
            logger.error("Object {} is null", name);
            throw new BadRequestException(ExceptionCode.NULL_OR_EMPTY.name(), String.format("Object %s cannot be null", name));
        }
    }
}
