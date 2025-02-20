package br.com.nlw_connect.events.controller;

import br.com.nlw_connect.events.model.Event;
import br.com.nlw_connect.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/events")
    public Event addNewEvent(@RequestBody Event newEvent) {
        return eventService.addNewEvent(newEvent);
    }

    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/events/{prettyName}")
    public ResponseEntity<Event> getEventByPrettyName(@PathVariable String prettyName) {
        Event evt = eventService.getByPrettyName(prettyName);
        if (evt != null) {
            return ResponseEntity.status(HttpStatus.OK).body(evt);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
