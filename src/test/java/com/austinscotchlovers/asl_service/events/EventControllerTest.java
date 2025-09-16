package com.austinscotchlovers.asl_service.events;

import com.austinscotchlovers.asl_service.events.dto.EventDto;
import com.austinscotchlovers.asl_service.events.mapper.EventMapper;
import com.austinscotchlovers.asl_service.exceptions.EventNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private EventMapper eventMapper;

    @Autowired
    private ObjectMapper objectMapper;


    private Event testEvent;
    private EventDto testEventDto;

    @BeforeEach
    void setUp() {
        testEvent = new Event("Test Event", "Test Description", LocalDate.now(), LocalTime.now(), "Test Location", new ArrayList<>());
        testEvent.setId(1L);
        testEventDto = new EventDto("Test Event", "Test Description", LocalDate.now(), LocalTime.now(), "Test Location");
    }

    @Test
    @WithMockUser
    void should_return_all_events_on_get_request() throws Exception {
        List<Event> allEvents = Collections.singletonList(testEvent);
        given(eventService.getAllEvents()).willReturn(allEvents);

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Event"));
    }

    @Test
    @WithMockUser
    void should_return_event_by_id() throws Exception {
        given(eventService.getEventById(1L)).willReturn(testEvent);

        mockMvc.perform(get("/api/v1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    @WithMockUser
    void should_create_event() throws Exception {
        given(eventService.saveEvent(any(EventDto.class))).willReturn(testEvent);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_update_event() throws Exception {
        Event updatedEvent = new Event("New Event", "New Desc", LocalDate.now(), LocalTime.now(), "New Location", new ArrayList<>());
        updatedEvent.setId(1L);
        EventDto updatedEventDto = new EventDto("New Event", "New Desc", LocalDate.now(), LocalTime.now(), "New Location");

        given(eventService.updateEvent(eq(1L), any(EventDto.class))).willReturn(updatedEvent);

        mockMvc.perform(put("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEventDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Event"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_delete_event() throws Exception {
        mockMvc.perform(delete("/api/v1/events/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(eventService, times(1)).deleteEvent(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_return_bad_request_on_invalid_create_request() throws Exception {
        EventDto invalidDto = new EventDto(null, "desc", null, null, null);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_return_bad_request_on_invalid_update_request() throws Exception {
        EventDto invalidDto = new EventDto(null, "desc", null, null, null);

        mockMvc.perform(put("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_not_found_on_get_request_for_nonexistent_event() throws Exception {
        given(eventService.getEventById(99L)).willThrow(new EventNotFoundException("Event not found."));

        mockMvc.perform(get("/api/v1/events/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_return_not_found_on_update_request_for_nonexistent_event() throws Exception {
        EventDto updatedDto = new EventDto("New Event", "New Desc", LocalDate.now(), LocalTime.now(), "New Location");
        given(eventService.updateEvent(eq(99L), any(EventDto.class))).willThrow(new EventNotFoundException("Event not found."));

        mockMvc.perform(put("/api/v1/events/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_return_not_found_on_delete_request_for_nonexistent_event() throws Exception {
        doThrow(new EventNotFoundException("Event not found.")).when(eventService).deleteEvent(99L);

        mockMvc.perform(delete("/api/v1/events/99")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_unauthorized_for_create_request_without_auth() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDto))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_unauthorized_for_update_request_without_auth() throws Exception {
        mockMvc.perform(put("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDto))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_unauthorized_for_delete_request_without_auth() throws Exception {
        mockMvc.perform(delete("/api/v1/events/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}