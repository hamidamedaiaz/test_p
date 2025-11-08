package fr.unice.polytech.sophiatecheats.domain.entities.user;

import fr.unice.polytech.sophiatecheats.domain.exceptions.InsufficientCreditException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithBasicInformation() {
        // Given
        String email = "test@unice.fr";
        String name = "Test User";

        // When
        User user = new User(email, name);

        // Then
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(name, user.getName());
        assertEquals(BigDecimal.ZERO, user.getStudentCredit());
    }

    @Test
    void shouldCreateUserWithCredit() {
        // Given
        UUID id = UUID.randomUUID();
        String email = "test@unice.fr";
        String name = "Test User";
        BigDecimal credit = new BigDecimal("50.00");

        // When
        User user = new User(id, email, name, credit);

        // Then
        assertEquals(id, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(name, user.getName());
        assertEquals(credit, user.getStudentCredit());
    }

    @Test
    void shouldAddCreditToUser() {
        // Given
        User user = new User("test@unice.fr", "Test User");
        BigDecimal creditToAdd = new BigDecimal("25.50");

        // When
        user.addCredit(creditToAdd);

        // Then
        assertEquals(creditToAdd, user.getStudentCredit());
    }

    @Test
    void shouldNotAddNegativeCredit() {
        // Given
        User user = new User("test@unice.fr", "Test User");
        BigDecimal negativeCredit = new BigDecimal("-10.00");

        // When
        user.addCredit(negativeCredit);

        // Then
        assertEquals(BigDecimal.ZERO, user.getStudentCredit());
    }

    @Test
    void shouldCheckIfUserHasEnoughCredit() {
        // Given
        User user = new User("test@unice.fr", "Test User");
        user.addCredit(new BigDecimal("50.00"));

        // When & Then
        assertTrue(user.hasEnoughCredit(new BigDecimal("30.00")));
        assertTrue(user.hasEnoughCredit(new BigDecimal("50.00")));
        assertFalse(user.hasEnoughCredit(new BigDecimal("60.00")));
    }

    @Test
    void shouldDeductCreditWhenSufficientFunds() {
        // Given
        User user = new User("test@unice.fr", "Test User");
        user.addCredit(new BigDecimal("50.00"));
        BigDecimal amountToDeduct = new BigDecimal("20.00");

        // When
        user.deductCredit(amountToDeduct);

        // Then
        assertEquals(new BigDecimal("30.00"), user.getStudentCredit());
    }

    @Test
    void shouldThrowExceptionWhenDeductingMoreThanAvailable() {
        // Given
        User user = new User("test@unice.fr", "Test User");
        user.addCredit(new BigDecimal("20.00"));
        BigDecimal amountToDeduct = new BigDecimal("30.00");

        // When & Then
        assertThrows(InsufficientCreditException.class, () -> {
            user.deductCredit(amountToDeduct);
        });
    }

    @Test
    void shouldBeEqualWhenSameId() {
        // Given
        UUID id = UUID.randomUUID();
        User user1 = new User(id, "test1@unice.fr", "User 1", BigDecimal.ZERO);
        User user2 = new User(id, "test2@unice.fr", "User 2", BigDecimal.TEN);

        // When & Then
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        // Given
        User user1 = new User("test1@unice.fr", "User 1");
        User user2 = new User("test2@unice.fr", "User 2");

        // When & Then
        assertNotEquals(user1, user2);
    }
}
