package ch.zhaw.fswd.powerdate.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.fswd.powerdate.dto.ProfileDto;
import ch.zhaw.fswd.powerdate.dto.InterestDto;
import ch.zhaw.fswd.powerdate.dto.MismatchDto;
import ch.zhaw.fswd.powerdate.entity.MismatchDbo;
import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import ch.zhaw.fswd.powerdate.repository.MismatchRepository;
import ch.zhaw.fswd.powerdate.repository.ProfileRepository;


@Service
public class MismatchController {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final MismatchRepository mismatchRepository;
    private final ProfileController profileController;
    private final ProfileRepository profileRepository;
    private final Random random = new Random();

    @Autowired
    public MismatchController(MismatchRepository mismatchRepository, ProfileController profileController, ProfileRepository profileRepository) {
        this.mismatchRepository = mismatchRepository;
        this.profileController = profileController;
        this.profileRepository = profileRepository;
    }

    public void createMismatches() {
        List<ProfileDto> profiles = profileController.getPublicProfiles();
        List<MismatchDto> mismatchesToday;
        List<MismatchDto> mismatchesAlltime = getMismatches();

        for (ProfileDto currentProfile : profiles) {
            if ("6e9b2f43-7c8a-4d3e-8a1b-9e9a74c6c8ff".equals(currentProfile.getUuid())) {
                // check to not generate matches with the admin user
                continue;
            }

            mismatchesToday = getMismatchesByToday();
            List<MismatchDto> userMismatchesToday = mismatchesToday.stream().filter(match -> match.getMatcher().getUuid().equals(currentProfile.getUuid())).collect(Collectors.toList());
            while (userMismatchesToday.size() < 2) {
                try {
                    ProfileDto generatedMismatch = generatePossibleMismatch(currentProfile, profiles, mismatchesAlltime);
                    MismatchDto savedDto = saveMismatch(currentProfile, generatedMismatch);

                    userMismatchesToday.add(savedDto);
                    mismatchesAlltime = getMismatches();
                } catch (Exception e) {
                    logger.info("Caught Exception: " + e);
                }
            }
        }
    }

    public List<MismatchDto> getMismatches() {
        return this.mismatchRepository.findAll().stream().map(dbo -> new MismatchDto().fromDbo(dbo)).toList();
    }

    public List<MismatchDto> getMismatchesByToday() {
        return this.mismatchRepository.findByDate(LocalDate.now()).stream().map(dbo -> new MismatchDto().fromDbo(dbo)).toList();
    }

    public List<UUID> getMismatchesByTodayAndMismatcherId(UUID uuid) {
        ProfileDbo profileDbo = profileRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
        return this.mismatchRepository.findUuidsByDateAndMismatcherId(LocalDate.now(), profileDbo);
    }

    ProfileDto generatePossibleMismatch(ProfileDto currentProfile, List<ProfileDto> profiles, List<MismatchDto> mismatchesAlltime) {
        List<ProfileDto> mutableProfiles = new ArrayList<>(profiles);
        List<ProfileDto> toBeRemoved = new ArrayList<>();

        for (ProfileDto possibleProfile : profiles) {

            // check if profiles are interested in each others gender
            if (!currentProfile.getGenderInterest().equals(possibleProfile.getGender()) || !possibleProfile.getGenderInterest().equals(currentProfile.getGender())) {
                toBeRemoved.add(possibleProfile);
            }

            // check if profiles have no matching interests
            List<InterestDto> currentProfileInterests = currentProfile.getInterests();
            List<InterestDto> possibleProfileInterests = possibleProfile.getInterests();
            if (hasCommonInterest(currentProfileInterests, possibleProfileInterests)) {
                toBeRemoved.add(possibleProfile);
            }

            // check if profiles have matched before
            boolean hasExistingMismatch = mismatchesAlltime.stream().anyMatch(mismatchDto -> mismatchDto.getMatcher().getUuid().equals(currentProfile.getUuid()) && mismatchDto.getMatchee().getUuid().equals(possibleProfile.getUuid()));
            if (hasExistingMismatch) {
                toBeRemoved.add(possibleProfile);
            }
        }

        mutableProfiles.removeAll(toBeRemoved);
        int randomIndex = random.nextInt(mutableProfiles.size());
        return mutableProfiles.get(randomIndex);
    }

    private boolean hasCommonInterest(List<InterestDto> currentProfileInterests, List<InterestDto> possibleProfileInterests) {
        List<Long> currentIds = currentProfileInterests.stream().map(InterestDto::getId).toList();

        return possibleProfileInterests.stream().map(InterestDto::getId).anyMatch(currentIds::contains);
    }

    MismatchDto saveMismatch(ProfileDto currentProfile, ProfileDto generatedMismatch) {
        ProfileDbo matchProfileDbo = profileRepository.findById(generatedMismatch.getUuid()).orElseThrow();
        ProfileDbo currentProfileDbo = profileRepository.findById(currentProfile.getUuid()).orElseThrow();

        MismatchDbo dboOne = new MismatchDbo(currentProfileDbo, matchProfileDbo, LocalDate.now());
        MismatchDbo dboTwo = new MismatchDbo(matchProfileDbo, currentProfileDbo, LocalDate.now());

        mismatchRepository.saveAll(List.of(dboOne, dboTwo));

        logger.info("Saved Mismatch with " + currentProfile.getDisplayName() + "(" + generatedMismatch.getUuid() + ") and " + generatedMismatch.getDisplayName() + "(" + currentProfile.getUuid() + ")");

        return new MismatchDto().fromDbo(dboOne);
    }

    public ProfileDto getPublicProfile(UUID mismatchUuid) {
        ProfileDbo profileDbo = mismatchRepository.findMismatcheeByUuid(mismatchUuid);
        if (profileDbo == null) {
            throw new EntityNotFoundException("Profile not found for UUID: " + mismatchUuid);
        }
        return new ProfileDto(profileDbo);
    }
}