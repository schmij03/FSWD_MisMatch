package ch.zhaw.fswd.powerdate.controller;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import ch.zhaw.fswd.powerdate.dto.InterestDto;
import ch.zhaw.fswd.powerdate.dto.RegisterDto;
import ch.zhaw.fswd.powerdate.entity.InterestDbo;
import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import ch.zhaw.fswd.powerdate.entity.UserDbo;
import ch.zhaw.fswd.powerdate.repository.InterestRepository;
import ch.zhaw.fswd.powerdate.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.fswd.powerdate.dto.ProfileDto;
import ch.zhaw.fswd.powerdate.repository.ProfileRepository;
import ch.zhaw.fswd.powerdate.repository.UserRepository;
import ch.zhaw.fswd.powerdate.exceptions.UUIDNotFoundException;
import ch.zhaw.fswd.powerdate.exceptions.UserNotFoundException;

@Service
public class ProfileController {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Autowired
    public ProfileController(ProfileRepository profileRepository, UserRepository userRepository, InterestRepository interestRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.interestRepository = interestRepository;
    }

    public List<ProfileDto> getPublicProfiles() {
        return profileRepository.findAll().stream()
            .filter(profile -> !profile.getUuid().equals(UUID.fromString("6e9b2f43-7c8a-4d3e-8a1b-9e9a74c6c8ff")))
            .map(ProfileDto::new)
            .toList();
    }


    

    public ProfileDto getPublicProfile(UUID uuid) {
        return new ProfileDto(
                profileRepository.findById(uuid)
                        .orElseThrow(UUIDNotFoundException::new));
    }

    public byte[] getPNGProfileImage(String uuid) {
        return Base64.getDecoder().decode(
                profileRepository.findById(UUID.fromString(uuid)).orElseThrow(
                                UUIDNotFoundException::new).getRawPNGImageData()
                        .replace("\r", "")
                        .replace("\n", "")
                        .replace(" ", "")
                );
    }

    public ProfileDto getProfileByLoginName(String loginName) {
        UserDbo u = userRepository.findById(loginName).orElseThrow(UserNotFoundException::new);
        return new ProfileDto(u.getProfile());
    }

    public void updateMyProfile(String loginName, ProfileDto profileDto) {
        UserDbo u = userRepository.findById(loginName).orElseThrow(UserNotFoundException::new);
        ProfileDbo p = u.getProfile();
        profileDto.updateEntity(p);
        if (profileDto.getInterests() != null) {
            List<InterestDbo> interests = interestRepository.findByIdIn(profileDto.getInterests().stream().map(InterestDto::getId).toList());
            p.setInterests(interests);
        }
        profileRepository.save(p);
    }

    public void registerProfile(RegisterDto registerDto) {
        List<InterestDbo> interests = registerDto.getInterests().stream().map(InterestDto::toDbo).toList();
        ProfileDbo profileDbo = registerDto.getProfileDbo(interests);
        UserDbo userDbo = registerDto.getUserDbo(profileDbo);
        profileRepository.save(profileDbo);
        userDbo.setPasswordHash(passwordHasher.hashPassword(registerDto.getPasswordHash()));
        userRepository.save(userDbo);
    }
}
