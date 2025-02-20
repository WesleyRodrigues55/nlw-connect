package br.com.nlw_connect.events.repository;

import br.com.nlw_connect.events.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {
    public User findByEmail(String email);
}
