package com.event.validator;

import com.event.exception.BadRequestException;
import com.event.exception.ExceptionCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StringValidator {

    private final Logger logger = LoggerFactory.getLogger(StringValidator.class);

    public void validateStringNotBlank(String value, String name) {
        if(StringUtils.isBlank(value)) {
            logger.error("Value of String {} is empty or null", name);
            throw new BadRequestException(ExceptionCode.NULL_OR_EMPTY.name(), String.format("Field %s cannot be null or empty", name));
        }
    }
}
