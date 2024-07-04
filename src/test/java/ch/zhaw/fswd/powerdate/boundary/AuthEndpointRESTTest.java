package ch.zhaw.fswd.powerdate.boundary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AuthEndpointREST.class)
class AuthEndpointRESTTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "adminUser", roles = "ADMIN")
    void get_WithAdminRole_ShouldReturnPrincipalName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/current"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("adminUser"));
    }

    @Test
    @WithMockUser(username = "regularUser", roles = "USER")
    void get_WithUserRole_ShouldReturnPrincipalName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/current"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("regularUser"));
    }

    @Test
    void get_WithUnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/current"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}