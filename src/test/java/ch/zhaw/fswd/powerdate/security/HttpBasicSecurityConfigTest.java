package ch.zhaw.fswd.powerdate.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

class HttpBasicSecurityConfigTest {

    private HttpBasicSecurityConfig config;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        config = new HttpBasicSecurityConfig();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void testCustomAuthenticationSuccessHandler() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        config.customAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, authentication);

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("{\"message\": \"Login successful\"}", response.getContentAsString());
    }

    @Test
    void testCustomLogoutSuccessHandler() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        config.customLogoutSuccessHandler().onLogoutSuccess(request, response, authentication);

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("{\"message\": \"Logout successful\"}", response.getContentAsString());
    }

    @Test
    void testCustomAuthExceptionHandler() throws Exception {
        config.customAuthExceptionHandler().commence(request, response, null);

        Assertions.assertEquals(401, response.getStatus());
        Assertions.assertEquals("{\"message\": \"Unauthorized\"}", response.getContentAsString());
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder encoder = HttpBasicSecurityConfig.passwordEncoder();
        String password = "testPassword";
        String encodedPassword = encoder.encode(password);

        Assertions.assertNotEquals(password, encodedPassword);
        Assertions.assertTrue(encoder.matches(password, encodedPassword));
    }
}