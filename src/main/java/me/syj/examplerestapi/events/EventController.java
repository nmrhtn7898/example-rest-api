package me.syj.examplerestapi.events;

import lombok.RequiredArgsConstructor;
import me.syj.examplerestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event save = eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(save.getId());
        EventResource eventResource = new EventResource(event);
        eventResource
                .add(linkTo(EventController.class).withRel("query-events"))
                .add(selfLinkBuilder.withRel("update-event"))
                .add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(selfLinkBuilder.toUri()).body(eventResource);

    }

    @GetMapping
    public ResponseEntity queryEvents(PagedResourcesAssembler<Event> assembler, Pageable pageable) {
        Page<Event> page = eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> pagedModel = assembler.toModel(page, EventResource::new);
        pagedModel.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (!optionalEvent.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (!optionalEvent.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        Event event = optionalEvent.get();
        modelMapper.map(eventDto, event);
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
