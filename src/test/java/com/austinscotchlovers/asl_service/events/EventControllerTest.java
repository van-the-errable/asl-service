package com.austinscotchlovers.asl_service.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void should_return_all_events_on_get_request() throws Exception {
        Event event = new Event("Test Event", "Test Description", LocalDate.now(), LocalTime.now(), "Test Location", new ArrayList<>());
        List<Event> allEvents = Collections.singletonList(event);
        given(eventService.getAllEvents()).willReturn(allEvents);

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Event"));
    }

    @Test
    @WithMockUser
    void should_return_event_by_id() throws Exception {
        Event event = new Event("Test Event", "Test Description", LocalDate.now(), LocalTime.now(), "Test Location", new ArrayList<>());
        event.setId(1L);
        given(eventService.getEventById(1L)).willReturn(event);

        mockMvc.perform(get("/api/v1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_update_event() throws Exception {
        Event updatedEvent = new Event("New Event", "New Desc", LocalDate.now(), LocalTime.now(), "New Location", new ArrayList<>());
        updatedEvent.setId(1L);

        given(eventService.updateEvent(eq(1L), any(Event.class))).willReturn(updatedEvent);

        mockMvc.perform(put("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvent))
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
}