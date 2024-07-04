package ch.zhaw.fswd.powerdate.boundary;

import ch.zhaw.fswd.powerdate.controller.MismatchController;
import ch.zhaw.fswd.powerdate.dto.ProfileDto;
import ch.zhaw.fswd.powerdate.entity.enums.Role;
import ch.zhaw.fswd.powerdate.security.HttpBasicSecurityConfig;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@WebMvcTest(MismatchEndpointREST.class)
@Import(HttpBasicSecurityConfig.class)
class MismatchEndpointRESTTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MismatchController mismatchController;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMatches_AsAdmin_ShouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/match/createMatches"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(mismatchController, Mockito.times(1)).createMismatches();
    }

    @Test
    @WithMockUser(roles = "USER")
    void createMatches_AsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/match/createMatches"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        Mockito.verify(mismatchController, Mockito.times(0)).createMismatches();
    }

    @Test
    void createMatches_AsUnauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/match/createMatches"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(mismatchController, Mockito.times(0)).createMismatches();
    }

    @Test
    @WithMockUser(roles = Role.ADMIN)
    void getMatches_AsAdmin_ShouldReturnOk() throws Exception {
        UUID userUuid = UUID.randomUUID();
        List<UUID> matches = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        Mockito.when(mismatchController.getMismatchesByTodayAndMismatcherId(userUuid)).thenReturn(matches);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match/getMatches")
                        .param("userUuid", userUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));

        Mockito.verify(mismatchController, Mockito.times(1)).getMismatchesByTodayAndMismatcherId(userUuid);
    }

    @Test
    @WithMockUser(roles = Role.USER)
    void getMatches_AsUser_ShouldReturnOk() throws Exception {
        UUID userUuid = UUID.randomUUID();
        List<UUID> matches = List.of(UUID.randomUUID());
        Mockito.when(mismatchController.getMismatchesByTodayAndMismatcherId(userUuid)).thenReturn(matches);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match/getMatches")
                        .param("userUuid", userUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        Mockito.verify(mismatchController, Mockito.times(1)).getMismatchesByTodayAndMismatcherId(userUuid);
    }

    @Test
    @WithMockUser(roles = Role.USER)
    void getMatches_UserNotFound_ShouldReturnNotFound() throws Exception {
        UUID userUuid = UUID.randomUUID();
        Mockito.when(mismatchController.getMismatchesByTodayAndMismatcherId(userUuid)).thenThrow(new EntityNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match/getMatches")
                        .param("userUuid", userUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(mismatchController, Mockito.times(1)).getMismatchesByTodayAndMismatcherId(userUuid);
    }

    @Test
    void getMatches_AsUnauthenticated_ShouldReturnUnauthorized() throws Exception {
        UUID userUuid = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match/getMatches")
                        .param("userUuid", userUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(mismatchController, Mockito.never()).getMismatchesByTodayAndMismatcherId(Mockito.any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMatcheeByUuid_AsUser_ShouldReturnOk() throws Exception {
        UUID matcheeUuid = UUID.randomUUID();
        ProfileDto profileDto = new ProfileDto(); // Create a sample ProfileDto
        profileDto.setUuid(matcheeUuid);
        profileDto.setDisplayName("John Doe");

        Mockito.when(mismatchController.getPublicProfile(matcheeUuid)).thenReturn(profileDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match/getMatchee/{uuid}", matcheeUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.uuid").value(matcheeUuid.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.displayName").value("John Doe"));

        Mockito.verify(mismatchController, Mockito.times(1)).getPublicProfile(matcheeUuid);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMatcheeByUuid_AsAdmin_ShouldReturnOk() throws Exception {
        UUID matcheeUuid = UUID.randomUUID();
        ProfileDto profileDto = new ProfileDto(); // Create a sample ProfileDto
        profileDto.setUuid(matcheeUuid);
        profileDto.setDisplayName("Jane Doe");

        Mockito.when(mismatchController.getPublicProfile(matcheeUuid)).thenReturn(profileDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match/getMatchee/{uuid}", matcheeUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.uuid").value(matcheeUuid.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.displayName").value("Jane Doe"));

        Mockito.verify(mismatchController, Mockito.times(1)).getPublicProfile(matcheeUuid);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMatcheeByUuid_UserNotFound_ShouldReturnNotFound() throws Exception {
        UUID matcheeUuid = UUID.randomUUID();

        Mockito.when(mismatchController.getPublicProfile(matcheeUuid)).thenThrow(new EntityNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match/getMatchee/{uuid}", matcheeUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(mismatchController, Mockito.times(1)).getPublicProfile(matcheeUuid);
    }

    @Test
    void getMatcheeByUuid_AsUnauthenticated_ShouldReturnUnauthorized() throws Exception {
        UUID matcheeUuid = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/match/getMatchee/{uuid}", matcheeUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(mismatchController, Mockito.never()).getPublicProfile(Mockito.any());
    }
}