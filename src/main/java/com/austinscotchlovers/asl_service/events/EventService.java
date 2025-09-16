package com.austinscotchlovers.asl_service.events;

import com.austinscotchlovers.asl_service.events.dto.EventDto;
import com.austinscotchlovers.asl_service.events.mapper.EventMapper;
import com.austinscotchlovers.asl_service.exceptions.EventNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository,  EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(eventRepository.findAll());
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
    }

    public Event saveEvent(EventDto eventDto) {
        Event event = eventMapper.fromDto(eventDto);
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, EventDto updatedEventDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
        eventMapper.updateEventFromDto(updatedEventDto, event);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }
}
