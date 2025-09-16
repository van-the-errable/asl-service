package com.austinscotchlovers.asl_service.events;

import com.austinscotchlovers.asl_service.events.dto.EventDto;
import com.austinscotchlovers.asl_service.events.mapper.EventMapper;
import com.austinscotchlovers.asl_service.exceptions.EventNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        event = new Event("Test Event", "A test description", LocalDate.now(), LocalTime.now(), "Test Location", new ArrayList<>());
        event.setId(1L);
        eventDto = new EventDto("Test Event", "A test description", LocalDate.now(), LocalTime.now(), "Test Location");
    }

    @Test
    void should_find_event_by_id() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        Event foundEvent = eventService.getEventById(1L);

        assertThat(foundEvent).isNotNull();
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void should_throw_exception_when_event_not_found() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.getEventById(1L));
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void should_save_event_from_dto() {
        when(eventMapper.fromDto(any(EventDto.class))).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event savedEvent = eventService.saveEvent(eventDto);

        assertThat(savedEvent).isNotNull();
        verify(eventMapper, times(1)).fromDto(eventDto);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void should_update_event_from_dto() {
        Event existingEvent = new Event("Old Name", "Old Desc", null, null, null, new ArrayList<>());
        existingEvent.setId(1L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(existingEvent);

        eventService.updateEvent(1L, eventDto);

        verify(eventRepository, times(1)).findById(1L);
        verify(eventMapper, times(1)).updateEventFromDto(eventDto, existingEvent);
        verify(eventRepository, times(1)).save(existingEvent);
    }

    @Test
    void should_throw_exception_when_updating_non_existent_event() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.updateEvent(1L, eventDto));
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void should_delete_event() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void should_throw_exception_when_deleting_nonexistent_event() {
        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(1L));

        verify(eventRepository, never()).deleteById(anyLong());
    }
}