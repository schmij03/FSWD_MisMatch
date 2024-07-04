package ch.zhaw.fswd.powerdate.security;

import ch.zhaw.fswd.powerdate.entity.UserDbo;
import ch.zhaw.fswd.powerdate.entity.enums.Role;
import ch.zhaw.fswd.powerdate.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

class DBUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DBUserDetailsService userDetailsService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        // Arrange
        String loginName = "testUser";
        String password = "testPassword";
        String role = Role.USER;

        UserDbo userDbo = new UserDbo();
        userDbo.setLoginName(loginName);
        userDbo.setPasswordHash(password);
        userDbo.setRoleName(role);

        Mockito.when(userRepository.findById(loginName)).thenReturn(Optional.of(userDbo));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginName);

        // Assert
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals(loginName, userDetails.getUsername());
        Assertions.assertEquals(password, userDetails.getPassword());
        Assertions.assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("USER")));

        Mockito.verify(userRepository).findById(loginName);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String loginName = "nonExistentUser";

        Mockito.when(userRepository.findById(loginName)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(loginName));

        Mockito.verify(userRepository).findById(loginName);
    }

    @Test
    void testLoadUserByUsername_MultipleAuthorities() {
        // Arrange
        String loginName = "adminUser";
        String password = "adminPassword";
        String role = Role.ADMIN;

        UserDbo userDbo = new UserDbo();
        userDbo.setLoginName(loginName);
        userDbo.setPasswordHash(password);
        userDbo.setRoleName(role);

        Mockito.when(userRepository.findById(loginName)).thenReturn(Optional.of(userDbo));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginName);

        // Assert
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals(loginName, userDetails.getUsername());
        Assertions.assertEquals(password, userDetails.getPassword());
        Assertions.assertEquals(1, userDetails.getAuthorities().size());
        Assertions.assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ADMIN)));

        Mockito.verify(userRepository).findById(loginName);
    }
}