package fr.unice.polytech.sophiatecheats.domain.services;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.exceptions.UserNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.List;

public class CampusUserService {
    private final UserRepository repository;

    public CampusUserService(UserRepository repository) {
        this.repository = repository;
    }

    public User registerCampusUser(String name, String email, BigDecimal balance) {
        User user = new User(email, name);
        if (balance != null && balance.compareTo(BigDecimal.ZERO) > 0) {
            user.addCredit(balance);
        }
        repository.save(user);
        return user;
    }

    public User getCampusUser(String name) {
        return repository.findAll().stream()
                .filter(u -> u.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found: " + name));
    }

    public List<User> listCampusUsers() {
        return repository.findAll();
    }
}
