package me.syj.examplerestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0) {
            errors.reject("wrongPrices", "prices are wrong");
        }
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        LocalDateTime beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        if (beginEnrollmentDateTime.isAfter(beginEventDateTime) || beginEnrollmentDateTime.isAfter(endEventDateTime) || beginEnrollmentDateTime.isAfter(closeEnrollmentDateTime)) {
            errors.rejectValue("beginEnrollmentDateTime", "wrongValue", "beginEnrollmentEventDateTime is wrong");
        }
        if (closeEnrollmentDateTime.isAfter(beginEventDateTime) || closeEnrollmentDateTime.isAfter(endEventDateTime)) {
            errors.rejectValue("closeEnrollmentDateTime", "wrongValue", "closeEnrollmentDateTime is wrong");
        }
        if (beginEventDateTime.isAfter(endEventDateTime)) {
            errors.rejectValue("beginEventDateTime", "wrongValue", "beginEventDateTime is wrong");
        }

    }

}
