package ch.zhaw.fswd.powerdate.controller;

import ch.zhaw.fswd.powerdate.dto.InterestDto;
import ch.zhaw.fswd.powerdate.dto.ProfileDto;
import ch.zhaw.fswd.powerdate.dto.RegisterDto;
import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import ch.zhaw.fswd.powerdate.entity.enums.Gender;
import ch.zhaw.fswd.powerdate.repository.ProfileRepository;
import ch.zhaw.fswd.powerdate.entity.UserDbo;
import ch.zhaw.fswd.powerdate.repository.UserRepository;
import ch.zhaw.fswd.powerdate.exceptions.UUIDNotFoundException;
import ch.zhaw.fswd.powerdate.exceptions.UserNotFoundException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class ProfileControllerTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileController profileController;

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
    void testGetPublicProfiles() {
        EasyRandom generator = new EasyRandom();
        ProfileDbo profileDbo1 = generator.nextObject(ProfileDbo.class);
        ProfileDbo profileDbo2 = generator.nextObject(ProfileDbo.class);
        Mockito.when(profileRepository.findAll()).thenReturn(Arrays.asList(profileDbo1, profileDbo2));

        List<ProfileDto> result = profileController.getPublicProfiles();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(profileRepository).findAll();
    }

    @Test
    void testGetPublicProfile() {
        EasyRandom generator = new EasyRandom();
        UUID uuid = UUID.randomUUID();
        ProfileDbo profileDbo = generator.nextObject(ProfileDbo.class);
        Mockito.when(profileRepository.findById(uuid)).thenReturn(Optional.of(profileDbo));

        ProfileDto result = profileController.getPublicProfile(uuid);

        Assertions.assertNotNull(result);
        Mockito.verify(profileRepository).findById(uuid);
    }

    @Test
    void testGetPublicProfileNotFound() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(profileRepository.findById(uuid)).thenReturn(Optional.empty());

        Assertions.assertThrows(UUIDNotFoundException.class, () -> profileController.getPublicProfile(uuid));
    }

    @Test
    void testGetPNGProfileImage() {
        UUID uuid = UUID.randomUUID();
        ProfileDbo profileDbo = new ProfileDbo();
        profileDbo.setRawPNGImageData("SGVsbG8gV29ybGQ="); // Base64 for "Hello World"
        Mockito.when(profileRepository.findById(uuid)).thenReturn(Optional.of(profileDbo));

        byte[] result = profileController.getPNGProfileImage(uuid.toString());

        Assertions.assertArrayEquals("Hello World".getBytes(), result);
    }

    @Test
    void testGetProfileByLoginName() {
        // Arrange
        EasyRandom generator = new EasyRandom();
        String loginName = "testUser";
        UserDbo userDbo = generator.nextObject(UserDbo.class);
        ProfileDbo profileDbo = generator.nextObject(ProfileDbo.class);
        userDbo.setProfile(profileDbo);
        Mockito.when(userRepository.findById(loginName)).thenReturn(Optional.of(userDbo));

        // Act
        ProfileDto result = profileController.getProfileByLoginName(loginName);

        // Assert
        Assertions.assertNotNull(result);
        Mockito.verify(userRepository).findById(loginName);
    }

    @Test
    void testGetProfileByLoginNameNotFound() {
        String loginName = "nonExistentUser";
        Mockito.when(userRepository.findById(loginName)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> profileController.getProfileByLoginName(loginName));
    }

    @Test
    void testUpdateMyProfile() {
        String loginName = "testUser";
        UserDbo userDbo = new UserDbo();
        ProfileDbo profileDbo = new ProfileDbo();
        userDbo.setProfile(profileDbo);
        Mockito.when(userRepository.findById(loginName)).thenReturn(Optional.of(userDbo));

        ProfileDto profileDTO = new ProfileDto();
        profileDTO.setDisplayName("New Name");

        profileController.updateMyProfile(loginName, profileDTO);

        Mockito.verify(userRepository).findById(loginName);
        Mockito.verify(profileRepository).save(profileDbo);
        Assertions.assertEquals("New Name", profileDbo.getDisplayName());
    }

    @Test
    void testRegisterProfile() {
        // Arrange
        EasyRandom generator = new EasyRandom();
        InterestDto interest = generator.nextObject(InterestDto.class);
        RegisterDto registerDto = getRegisterDto();
        registerDto.setInterests(List.of(interest));

        // Act
        profileController.registerProfile(registerDto);

        // Assert
        Mockito.verify(profileRepository).save(Mockito.argThat(profileDbo ->
                profileDbo.getDisplayName().equals(registerDto.getDisplayName()) &&
                        profileDbo.getUserIntro().equals(registerDto.getUserIntro()) &&
                        profileDbo.getEmail().equals(registerDto.getEmail()) &&
                        profileDbo.getMobile().equals(registerDto.getMobile()) &&
                        profileDbo.getBirthdate().equals(registerDto.getBirthdate()) &&
                        profileDbo.getGender().equals(registerDto.getGender()) &&
                        profileDbo.getZip().equals(registerDto.getZip()) &&
                        profileDbo.getCity().equals(registerDto.getCity()) &&
                        profileDbo.getRawPNGImageData().equals(registerDto.getRawPNGImageData()) &&
                        profileDbo.getInterests() != null && profileDbo.getInterests().size() == 1
        ));

        Mockito.verify(userRepository).save(Mockito.argThat(userDbo ->
                userDbo.getLoginName().equals(registerDto.getLoginName()) &&
                        userDbo.getPasswordHash() != null && !userDbo.getPasswordHash().isEmpty() &&
                        userDbo.getRoleName().equals("ROLE_USER") &&
                        userDbo.getProfile() != null
        ));

    }

    private RegisterDto getRegisterDto() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setDisplayName("Test User");
        registerDto.setLoginName("testuser");
        registerDto.setPasswordHash("hashedpassword");
        registerDto.setUserIntro("Hello, I'm a test user");
        registerDto.setEmail("testuser@example.com");
        registerDto.setMobile("+1234567890");
        registerDto.setBirthdate(LocalDate.of(1990, 1, 1));
        registerDto.setGender(Gender.MALE);
        registerDto.setGenderInterest(Gender.FEMALE);
        registerDto.setZip((short) 12345);
        registerDto.setCity("Test City");
        registerDto.setRawPNGImageData("base64encodedimage");
        return registerDto;
    }
}