package com.austinscotchlovers.asl_service.events;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void should_save_an_event() {
        Event event = new Event("Summer Social",
                "An outdoor social gathering.",
                LocalDate.of(2025, 8, 28),
                LocalTime.of(18, 0),
                "Zilker Park",
                new ArrayList<>());

        Event savedEvent = eventRepository.save(event);

        assertThat(savedEvent).isNotNull();
        assertThat(savedEvent.getId()).isNotNull();
    }

    @Test
    void should_find_all_events() {
        eventRepository.save(new Event("Event 1", "Desc 1", LocalDate.now(), LocalTime.now(), "Location 1", new ArrayList<>()));
        eventRepository.save(new Event("Event 2", "Desc 2", LocalDate.now(), LocalTime.now(), "Location 2", new ArrayList<>()));

        Iterable<Event> events = eventRepository.findAll();

        assertThat(events).hasSize(2);
    }

    @Test
    void should_find_event_by_id() {
        Event event = new Event("Test Event", "Description", LocalDate.now(), LocalTime.now(), "Location", new ArrayList<>());
        Event savedEvent = eventRepository.save(event);

        Optional<Event> foundEvent = eventRepository.findById(savedEvent.getId());

        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getName()).isEqualTo("Test Event");
    }

    @Test
    void should_delete_event_by_id() {
        Event event = new Event("Test Event", "Description", LocalDate.now(), LocalTime.now(), "Location", new ArrayList<>());
        Event savedEvent = eventRepository.save(event);

        eventRepository.deleteById(savedEvent.getId());

        assertThat(eventRepository.findById(savedEvent.getId())).isNotPresent();
    }
}