package ch.zhaw.fswd.powerdate.boundary;

import ch.zhaw.fswd.powerdate.controller.ProfileController;
import ch.zhaw.fswd.powerdate.dto.ProfileDto;
import ch.zhaw.fswd.powerdate.dto.RegisterDto;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

class ProfileEndpointRESTTest {

    @Mock
    private ProfileController profileController;

    @InjectMocks
    private ProfileEndpointREST profileEndpointREST;

    @Mock
    private Principal principal;

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
    void getAllProfiles_ShouldReturnListOfProfiles() {
        // Arrange
        EasyRandom generator = new EasyRandom();
        List<ProfileDto> expectedProfiles = Arrays.asList(
                generator.nextObject(ProfileDto.class),
                generator.nextObject(ProfileDto.class)
        );
        Mockito.when(profileController.getPublicProfiles()).thenReturn(expectedProfiles);

        // Act
        List<ProfileDto> result = profileEndpointREST.getAllProfiles();

        // Assert
        Assertions.assertEquals(expectedProfiles, result);
    }

    @Test
    void getProfile_ShouldReturnProfile_WhenExists() {
        // Arrange
        EasyRandom generator = new EasyRandom();
        UUID uuid = UUID.randomUUID();
        ProfileDto expectedProfile = generator.nextObject(ProfileDto.class);
        Mockito.when(profileController.getPublicProfile(uuid)).thenReturn(expectedProfile);

        // Act
        ResponseEntity<ProfileDto> response = profileEndpointREST.getProfile(uuid);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedProfile, response.getBody());
    }

    @Test
    void getProfile_ShouldReturnNotFound_WhenDoesNotExist() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Mockito.when(profileController.getPublicProfile(uuid)).thenThrow(new RuntimeException("Profile not found"));

        // Act
        ResponseEntity<ProfileDto> response = profileEndpointREST.getProfile(uuid);

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getMyProfile_ShouldReturnNotFound_WhenExceptionOccurs() {
        // Arrange
        String username = "testUser";
        Mockito.when(principal.getName()).thenReturn(username);
        Mockito.when(profileController.getProfileByLoginName(username)).thenThrow(new RuntimeException("Profile not found"));

        // Act
        ResponseEntity<ProfileDto> response = profileEndpointREST.getMyProfile(principal);

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    void getMyProfile_ShouldReturnProfile_WhenExists() {
        // Arrange
        EasyRandom generator = new EasyRandom();
        String username = "testUser";
        ProfileDto expectedProfile = generator.nextObject(ProfileDto.class);
        Mockito.when(principal.getName()).thenReturn(username);
        Mockito.when(profileController.getProfileByLoginName(username)).thenReturn(expectedProfile);

        // Act
        ResponseEntity<ProfileDto> response = profileEndpointREST.getMyProfile(principal);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedProfile, response.getBody());
    }

    @Test
    void updateMyProfile_ShouldCallProfileController() {
        // Arrange
        EasyRandom generator = new EasyRandom();
        String username = "testUser";
        ProfileDto profile = generator.nextObject(ProfileDto.class);
        Mockito.when(principal.getName()).thenReturn(username);

        // Act
        profileEndpointREST.updateMyProfile(principal, profile);

        // Assert
        Mockito.verify(profileController, Mockito.times(1)).updateMyProfile(username, profile);
    }

    @Test
    void getProfileImage_ShouldReturnImage_WhenExists() {
        // Arrange
        String uuid = UUID.randomUUID().toString();
        byte[] expectedImage = new byte[]{1, 2, 3, 4, 5};
        Mockito.when(profileController.getPNGProfileImage(uuid)).thenReturn(expectedImage);

        // Act
        ResponseEntity<byte[]> response = profileEndpointREST.getProfileImage(uuid);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedImage, response.getBody());
    }

    @Test
    void getProfileImage_ShouldReturnNotFound_WhenExceptionOccurs() {
        // Arrange
        String uuid = UUID.randomUUID().toString();
        Mockito.when(profileController.getPNGProfileImage(uuid)).thenThrow(new RuntimeException("Image not found"));

        // Act
        ResponseEntity<byte[]> response = profileEndpointREST.getProfileImage(uuid);

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    void getProfileImage_ShouldReturnImageWithCorrectHeaders_WhenImageExists() {
        // Arrange
        String uuid = UUID.randomUUID().toString();
        byte[] expectedImage = new byte[]{1, 2, 3, 4, 5};
        Mockito.when(profileController.getPNGProfileImage(uuid)).thenReturn(expectedImage);

        // Act
        ResponseEntity<byte[]> response = profileEndpointREST.getProfileImage(uuid);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
        Assertions.assertEquals(expectedImage, response.getBody());
    }

    @Test
    void register_ShouldCallProfileController() {
        // Arrange
        EasyRandom generator = new EasyRandom();
        RegisterDto registerDto = generator.nextObject(RegisterDto.class);

        // Act
        ResponseEntity<Void> response = profileEndpointREST.register(registerDto);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(profileController, Mockito.times(1)).registerProfile(registerDto);
    }
}