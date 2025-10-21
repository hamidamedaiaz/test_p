package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implémentation en mémoire du repository utilisateur.
 * Utilisée pour les tests et le développement.
 */
public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        initializeTestUsers();
    }

    private void initializeTestUsers() {
        User student1 = new User(UUID.randomUUID(), "etudiant1@unice.fr", "Jean Dupont", new BigDecimal("50.00"));
        User student2 = new User(UUID.randomUUID(), "etudiant2@unice.fr", "Marie Martin", new BigDecimal("75.50"));
        User student3 = new User(UUID.randomUUID(), "etudiant3@unice.fr", "Pierre Durand", new BigDecimal("25.00"));

        users.put(student1.getId(), student1);
        users.put(student2.getId(), student2);
        users.put(student3.getId(), student3);
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean deleteById(UUID id) {
        return users.remove(id) != null;
    }

    @Override
    public boolean existsById(UUID id) {
        return users.containsKey(id);
    }

    public long count() {
        return users.size();
    }

    public void clear() {
        users.clear();
    }

    public User createTestUser(String email, String name, BigDecimal credit) {
        User user = new User(email, name);
        if (credit != null && credit.compareTo(BigDecimal.ZERO) > 0) {
            user.addCredit(credit);
        }
        return save(user);
    }
}
