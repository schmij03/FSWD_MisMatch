package ch.zhaw.fswd.powerdate.controller;

import ch.zhaw.fswd.powerdate.dto.InterestDto;
import ch.zhaw.fswd.powerdate.dto.MismatchDto;
import ch.zhaw.fswd.powerdate.dto.ProfileDto;
import ch.zhaw.fswd.powerdate.entity.MismatchDbo;
import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import ch.zhaw.fswd.powerdate.entity.enums.Gender;
import ch.zhaw.fswd.powerdate.repository.MismatchRepository;
import ch.zhaw.fswd.powerdate.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.*;
import java.util.stream.Collectors;

class MismatchControllerTest {

    @Mock
    private MismatchRepository mismatchRepository;

    @Mock
    private ProfileController profileController;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private MismatchController mismatchController;

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
    void createMatches_ShouldCreateMismatchesForAllProfiles() {
        // Arrange
        List<ProfileDto> profiles = createMockProfiles();
        Mockito.when(profileController.getPublicProfiles()).thenReturn(profiles);
        Mockito.when(mismatchRepository.findAll()).thenReturn(new ArrayList<>());
        Mockito.when(mismatchRepository.findByDate(Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(profileRepository.findById(Mockito.any())).thenReturn(Optional.of(new ProfileDbo()));

        // Act
        mismatchController.createMismatches();

        // Assert
        Mockito.verify(mismatchRepository, Mockito.atLeastOnce()).saveAll(Mockito.anyList());
    }

    @Test
    void getMatches_ShouldReturnAllMismatches() {
        // Arrange
        List<MismatchDbo> mismatchDbos = createMockMismatchDbos();
        Mockito.when(mismatchRepository.findAll()).thenReturn(mismatchDbos);

        // Act
        List<MismatchDto> result = mismatchController.getMismatches();

        // Assert
        Assertions.assertEquals(mismatchDbos.size(), result.size());
    }

    @Test
    void getMismatchesByToday_ShouldReturnTodayMismatches() {
        // Arrange
        List<MismatchDbo> todayMismatchDbos = createMockMismatchDbos();
        Mockito.when(mismatchRepository.findByDate(LocalDate.now())).thenReturn(todayMismatchDbos);

        // Act
        List<MismatchDto> result = mismatchController.getMismatchesByToday();

        // Assert
        Assertions.assertEquals(todayMismatchDbos.size(), result.size());
    }

    @Test
    void getMismatchesByTodayAndMismatcherId_ShouldReturnMismatchesForUser() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        ProfileDbo profileDbo = new ProfileDbo();
        List<UUID> userMismatchDbos = List.of(UUID.randomUUID());
        Mockito.when(profileRepository.findById(uuid)).thenReturn(Optional.of(profileDbo));
        Mockito.when(mismatchRepository.findUuidsByDateAndMismatcherId(LocalDate.now(), profileDbo)).thenReturn(userMismatchDbos);

        // Act
        List<UUID> result = mismatchController.getMismatchesByTodayAndMismatcherId(uuid);

        // Assert
        Assertions.assertEquals(userMismatchDbos.size(), result.size());
    }

    @Test
    void getMismatchesByTodayAndMismatcherId_ShouldThrowEntityNotFoundException() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Mockito.when(profileRepository.findById(uuid)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> mismatchController.getMismatchesByTodayAndMismatcherId(uuid));
    }

    @Test
    void generatePossibleMismatch_ShouldReturnValidMismatch() {
        // Arrange
        List<ProfileDto> profiles = createMockProfiles();
        ProfileDto currentProfile = profiles.getFirst();
        List<MismatchDto> existingMismatches = new ArrayList<>();

        // Act
        ProfileDto result = mismatchController.generatePossibleMismatch(currentProfile, profiles, existingMismatches);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertNotEquals(currentProfile.getUuid(), result.getUuid());
        Assertions.assertEquals(currentProfile.getGenderInterest(), result.getGender());
        Assertions.assertEquals(result.getGenderInterest(), currentProfile.getGender());
        Assertions.assertTrue(hasNoCommonInterest(currentProfile.getInterests(), result.getInterests()));
    }

    @Test
    void saveMismatch_ShouldSaveMismatchAndReturnDto() {
        // Arrange
        ProfileDto currentProfile = createMockProfiles().get(0);
        ProfileDto generatedMismatch = createMockProfiles().get(1);
        ProfileDbo currentProfileDbo = new ProfileDbo();
        ProfileDbo matchProfileDbo = new ProfileDbo();

        Mockito.when(profileRepository.findById(currentProfile.getUuid())).thenReturn(Optional.of(currentProfileDbo));
        Mockito.when(profileRepository.findById(generatedMismatch.getUuid())).thenReturn(Optional.of(matchProfileDbo));

        // Act
        MismatchDto result = mismatchController.saveMismatch(currentProfile, generatedMismatch);

        // Assert
        Assertions.assertNotNull(result);
        Mockito.verify(mismatchRepository, Mockito.times(1)).saveAll(Mockito.anyList());
    }

    @Test
    void getPublicProfile_ShouldReturnProfileDto() {
        // Arrange
        EasyRandom generator = new EasyRandom();
        UUID mismatchUuid = UUID.randomUUID();
        ProfileDbo profileDbo = generator.nextObject(ProfileDbo.class);
        profileDbo.setUuid(mismatchUuid);

        Mockito.when(mismatchRepository.findMismatcheeByUuid(mismatchUuid)).thenReturn(profileDbo);

        // Act
        ProfileDto result = mismatchController.getPublicProfile(mismatchUuid);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(mismatchUuid, result.getUuid());
        Assertions.assertEquals(profileDbo.getDisplayName(), result.getDisplayName());
        Assertions.assertEquals(profileDbo.getGender(), result.getGender());
        Assertions.assertEquals(profileDbo.getGenderInterest(), result.getGenderInterest());

        Mockito.verify(mismatchRepository, Mockito.times(1)).findMismatcheeByUuid(mismatchUuid);
    }

    @Test
    void getPublicProfile_ShouldThrowEntityNotFoundException() {
        // Arrange
        UUID mismatchUuid = UUID.randomUUID();
        Mockito.when(mismatchRepository.findMismatcheeByUuid(mismatchUuid)).thenReturn(null);

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> mismatchController.getPublicProfile(mismatchUuid));
        Mockito.verify(mismatchRepository, Mockito.times(1)).findMismatcheeByUuid(mismatchUuid);
    }

    private List<ProfileDto> createMockProfiles() {
        ProfileDto profile1 = new ProfileDto();
        profile1.setUuid(UUID.randomUUID());
        profile1.setDisplayName("User1");
        profile1.setGender(Gender.MALE);
        profile1.setGenderInterest(Gender.FEMALE);
        profile1.setInterests(Arrays.asList(new InterestDto(1L, "Interest1", "Example 1"), new InterestDto(2L, "Interest2", "Example 2")));

        ProfileDto profile2 = new ProfileDto();
        profile2.setUuid(UUID.randomUUID());
        profile2.setDisplayName("User2");
        profile2.setGender(Gender.FEMALE);
        profile2.setGenderInterest(Gender.MALE);
        profile2.setInterests(Arrays.asList(new InterestDto(3L, "Interest3", "Example 3"), new InterestDto(4L, "Interest4", "Example4")));

        return Arrays.asList(profile1, profile2);
    }

    private List<MismatchDbo> createMockMismatchDbos() {
        MismatchDbo mismatch1 = new MismatchDbo();
        MismatchDbo mismatch2 = new MismatchDbo();
        return Arrays.asList(mismatch1, mismatch2);
    }

    private boolean hasNoCommonInterest(List<InterestDto> interests1, List<InterestDto> interests2) {
        Set<Long> ids1 = interests1.stream().map(InterestDto::getId).collect(Collectors.toSet());
        Set<Long> ids2 = interests2.stream().map(InterestDto::getId).collect(Collectors.toSet());
        return Collections.disjoint(ids1, ids2);
    }
}