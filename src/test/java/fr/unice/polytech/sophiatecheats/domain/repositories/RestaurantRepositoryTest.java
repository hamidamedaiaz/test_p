package fr.unice.polytech.sophiatecheats.domain.repositories;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.exceptions.DuplicateRestaurantException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantValidationException;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantRepositoryTest {

    private InMemoryRestaurantRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryRestaurantRepository();
    }

    @Test
    void testSaveAndFindById() {
        Restaurant r = new Restaurant("CROUS", "Valbonne");
        repo.save(r);
        var found = repo.findById(r.getId());
        assertTrue(found.isPresent());
        assertEquals("CROUS", found.get().getName());
        assertEquals("Valbonne", found.get().getAddress());
    }

    @Test
    void testSaveDuplicateRestaurantNameThrowsException() {
        Restaurant r1 = new Restaurant("CROUS", "Valbonne");
        Restaurant r2 = new Restaurant("Crous", "vaLBonne");
        repo.save(r1);
        assertThrows(DuplicateRestaurantException.class, () -> repo.save(r2));
    }

    @Test
    void testInvalidRestaurantNameThrowsValidationException() {
        assertThrows(RestaurantValidationException.class, () -> new Restaurant("", "Nice"));
    }

    @Test
    void testFindAllReturnsAllRestaurants() {
        Restaurant r1 = new Restaurant("CROUS", "Valbonne");
        Restaurant r2 = new Restaurant("Mensa", "Nice");
        repo.save(r1);
        repo.save(r2);
        assertEquals(5, repo.findAll().size()); // 2 + 3 more samples restaurants in initialization
    }

    @Test
    void testFindByIdReturnsEmptyIfNotFound() {
        assertTrue(repo.findById(UUID.randomUUID()).isEmpty());
    }
}