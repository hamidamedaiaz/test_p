package fr.unice.polytech.sophiatecheats.domain.services;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.exceptions.UserNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CampusUserServiceTest {

    private UserRepository userRepo;
    private CampusUserService userService;

    @BeforeEach
    void setUp() {
        userRepo = Mockito.mock(UserRepository.class);
        userService = new CampusUserService(userRepo);
    }

    @Test
    void testRegisterCampusUserWithBalance() {
        User user = userService.registerCampusUser("Alice", "alice@mail.com", BigDecimal.valueOf(100));
        assertEquals("Alice", user.getName());
        assertEquals("alice@mail.com", user.getEmail());
        assertEquals(BigDecimal.valueOf(100), user.getStudentCredit());
        verify(userRepo, times(1)).save(user);
    }

    @Test
    void testRegisterCampusUserWithoutBalance() {
        User user = userService.registerCampusUser("Bob", "bob@mail.com", null);
        assertEquals(BigDecimal.ZERO, user.getStudentCredit());
        verify(userRepo, times(1)).save(user);
    }

    @Test
    void testGetCampusUserFound() {
        User user = new User("charlie@mail.com", "Charlie");
        when(userRepo.findAll()).thenReturn(List.of(user));

        User found = userService.getCampusUser("Charlie");
        assertEquals("Charlie", found.getName());
    }

    @Test
    void testGetCampusUserNotFound() {
        when(userRepo.findAll()).thenReturn(List.of());
        assertThrows(UserNotFoundException.class, () -> userService.getCampusUser("Nobody"));
    }

    @Test
    void testListCampusUsers() {
        User u1 = new User("u1@mail.com", "U1");
        User u2 = new User("u2@mail.com", "U2");
        when(userRepo.findAll()).thenReturn(List.of(u1, u2));

        List<User> users = userService.listCampusUsers();
        assertEquals(2, users.size());
    }
}
