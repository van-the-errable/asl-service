package com.austinscotchlovers.asl_service.events;

import com.austinscotchlovers.asl_service.exceptions.EventNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private EventService eventService;

    @Test
    void should_find_event_by_id() {
        Event event = new Event();

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
    void should_update_event() {
        Event existingEvent = new Event("Old Name", "Old Desc", null, null, null, new ArrayList<>());
        existingEvent.setId(1L);
        Event updatedEvent = new Event("New Name", "New Desc", null, null, null, new ArrayList<>());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);

        Event result = eventService.updateEvent(1L, updatedEvent);

        assertThat(result.getName()).isEqualTo("New Name");
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void should_delete_event() {
        when(eventRepository.existsById(anyLong())).thenReturn(true);
        eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void should_throw_exception_when_deleting_non_existent_event() {
        when(eventRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(1L));
        verify(eventRepository, times(0)).deleteById(1L);
    }
}