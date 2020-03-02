package me.syj.examplerestapi.events;

import me.syj.examplerestapi.common.BaseControllerTest;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    @DisplayName("정삭적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 2, 2, 16, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 2, 3, 16, 21))
                .beginEventDateTime(LocalDateTime.of(2020, 2, 10, 16, 21))
                .endEventDateTime(LocalDateTime.of(2020, 2, 11, 16, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to the created event"),
                                linkWithRel("query-events").description("link to query event"),
                                linkWithRel("update-event").description("link to update the event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        responseHeaders(
                              headerWithName(HttpHeaders.LOCATION).description("location header"),
                              headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells is this event is free or not"),
                                fieldWithPath("offline").description("it tells is this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 2, 2, 16, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 2, 3, 16, 21))
                .beginEventDateTime(LocalDateTime.of(2020, 2, 10, 16, 21))
                .endEventDateTime(LocalDateTime.of(2020, 2, 11, 16, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 2, 4, 16, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 2, 3, 16, 21))
                .beginEventDateTime(LocalDateTime.of(2020, 2, 12, 16, 21))
                .endEventDateTime(LocalDateTime.of(2020, 2, 11, 16, 21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번쨰 페이지 조회 테스트")
    public void queryEvents() throws Exception {
        // given
        IntStream.range(0, 30).forEach(i -> generateEvent());
        // when
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("page", "1");
        map.add("size", "10");
        map.add("sort", "name,desc");
        // then
        mockMvc.perform(get("/api/events")
                .params(map)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[*]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("first").description("link to first page"),
                                linkWithRel("last").description("link to last page"),
                                linkWithRel("prev").description("link to prev page"),
                                linkWithRel("next").description("link to next page"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                        ),
                        requestParameters(
                                parameterWithName("page").description("number of page"),
                                parameterWithName("size").description("size of page"),
                                parameterWithName("sort").description("direction of sorting")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.eventList[*].id").description("identifier of event"),
                                fieldWithPath("_embedded.eventList[*].name").description("name of event"),
                                fieldWithPath("_embedded.eventList[*].description").description("description of event"),
                                fieldWithPath("_embedded.eventList[*].beginEnrollmentDateTime").description("date time of begin enrollment of event"),
                                fieldWithPath("_embedded.eventList[*].closeEnrollmentDateTime").description("date time of close enrollment of event"),
                                fieldWithPath("_embedded.eventList[*].beginEventDateTime").description("date time of begin of event"),
                                fieldWithPath("_embedded.eventList[*].endEventDateTime").description("date time of end of event"),
                                fieldWithPath("_embedded.eventList[*].location").description("location of event"),
                                fieldWithPath("_embedded.eventList[*].basePrice").description("base price of event"),
                                fieldWithPath("_embedded.eventList[*].maxPrice").description("max price of event"),
                                fieldWithPath("_embedded.eventList[*].limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("_embedded.eventList[*].offline").description("it tells is this event is free or not"),
                                fieldWithPath("_embedded.eventList[*].free").description("it tells is this event is offline event or not"),
                                fieldWithPath("_embedded.eventList[*].eventStatus").description("event status"),
                                fieldWithPath("_embedded.eventList[*]._links.self.href").description("link to self"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.first.href").description("link to first page"),
                                fieldWithPath("_links.last.href").description("link to last page"),
                                fieldWithPath("_links.prev.href").description("link to prev page"),
                                fieldWithPath("_links.next.href").description("link to next page"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("page.number").description("number of page"),
                                fieldWithPath("page.size").description("size of page"),
                                fieldWithPath("page.totalElements").description("total count of all elements"),
                                fieldWithPath("page.totalPages").description("total count of all pages")
                        )
                ));
    }

    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        // given
        Event event = generateEvent();
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/events/{id}", event.getId())
                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(print())
                .andDo(document("get-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                        ),
                        pathParameters(
                                parameterWithName("id").description("identifier of event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of event"),
                                fieldWithPath("name").description("name of event"),
                                fieldWithPath("description").description("description of event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of event"),
                                fieldWithPath("endEventDateTime").description("date time of end of event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base price of event"),
                                fieldWithPath("maxPrice").description("max price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("offline").description("it tells is this event is free or not"),
                                fieldWithPath("free").description("it tells is this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @Test
    @DisplayName("없는 이벤트는 조회했을 때 404 응답받기")
    public void getEvent_404() throws Exception {
        // given
        Event event = generateEvent();
        // when & then
        mockMvc.perform(get("/api/events/11833"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        // given
        Event event = generateEvent();
        String eventName = "update event";
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setName(eventName);
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of event"),
                                fieldWithPath("description").description("description of event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of event"),
                                fieldWithPath("endEventDateTime").description("date time of end of event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base price of event"),
                                fieldWithPath("maxPrice").description("max price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        pathParameters(
                                parameterWithName("id").description("identifier of event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of event"),
                                fieldWithPath("name").description("name of event"),
                                fieldWithPath("description").description("description of event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of event"),
                                fieldWithPath("endEventDateTime").description("date time of end of event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base price of event"),
                                fieldWithPath("maxPrice").description("max price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("offline").description("it tells is this event is free or not"),
                                fieldWithPath("free").description("it tells is this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @Test
    @DisplayName("입력값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEvent_BadRequest_Empty_Input() throws Exception {
        // given
        Event event = generateEvent();
        EventDto eventDto = new EventDto();
        // when & then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent_BadRequest_Wrong_Input() throws Exception {
        // given
        Event event = generateEvent();
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setBasePrice(1000);
        // when & then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 수정 실패")
    public void updateEvent_Notfound_Not_Exist() throws Exception {
        // given
        Event event = generateEvent();
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        // when & then
        mockMvc.perform(put("/api/events/123123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    private Event generateEvent() {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 2, 2, 16, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 2, 3, 16, 21))
                .beginEventDateTime(LocalDateTime.of(2020, 2, 10, 16, 21))
                .endEventDateTime(LocalDateTime.of(2020, 2, 11, 16, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();
        event.update();
        return eventRepository.save(event);
    }

}
