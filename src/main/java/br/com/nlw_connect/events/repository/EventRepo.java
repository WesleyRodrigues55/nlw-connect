package br.com.nlw_connect.events.repository;

import br.com.nlw_connect.events.model.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepo extends CrudRepository<Event, Integer> {
    public Event findByPrettyName(String prettyName);
}
