package ch.zhaw.fswd.powerdate.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class PasswordHasherTest {

    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        passwordHasher = new PasswordHasher();
    }

    @Test
    void hashPassword_shouldReturnDifferentHashForSamePassword() {
        String password = "myPassword123";
        String hash1 = passwordHasher.hashPassword(password);
        String hash2 = passwordHasher.hashPassword(password);

        Assertions.assertNotEquals(hash1, hash2);
    }

    @Test
    void hashPassword_shouldReturnNonNullHash() {
        String password = "anotherPassword456";
        String hash = passwordHasher.hashPassword(password);

        Assertions.assertNotNull(hash);
        Assertions.assertFalse(hash.isEmpty());
    }

    @Test
    void verifyPassword_shouldReturnTrueForCorrectPassword() {
        String password = "correctPassword789";
        String hash = passwordHasher.hashPassword(password);

        Assertions.assertTrue(passwordHasher.verifyPassword(password, hash));
    }

    @Test
    void verifyPassword_shouldReturnFalseForIncorrectPassword() {
        String correctPassword = "correctPassword789";
        String incorrectPassword = "wrongPassword123";
        String hash = passwordHasher.hashPassword(correctPassword);

        Assertions.assertFalse(passwordHasher.verifyPassword(incorrectPassword, hash));
    }
}