package ch.zhaw.fswd.powerdate.dto;


import java.util.List;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import ch.zhaw.fswd.powerdate.entity.enums.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileDto {
    private UUID uuid;
    private String displayName;
    private String email;
    private String mobile;
    private Integer age;
    private Gender gender;
    private String userIntro;
    private Short zip;
    private String city;
    private List<InterestDto> interests;
    private Gender genderInterest;

    public ProfileDto(ProfileDbo profileDbo) {
        this.uuid = profileDbo.getUuid();
        this.displayName = profileDbo.getDisplayName();
        this.email = profileDbo.getEmail();
        this.mobile = profileDbo.getMobile();
        this.age = Period.between(profileDbo.getBirthdate(), LocalDate.now()).getYears();
        this.gender = profileDbo.getGender();
        this.userIntro = profileDbo.getUserIntro();
        this.interests = profileDbo.getInterests().stream().map(dbo -> new InterestDto().fromDbo(dbo)).toList();
        this.genderInterest = profileDbo.getGenderInterest();
        this.zip = profileDbo.getZip();
        this.city = profileDbo.getCity();
    }

    public void updateEntity(ProfileDbo profileDbo) {
        profileDbo.setDisplayName(this.displayName);
        profileDbo.setEmail(this.email);
        profileDbo.setMobile(this.mobile);
        profileDbo.setGender(this.gender);
        profileDbo.setUserIntro(this.userIntro);
        profileDbo.setGenderInterest(genderInterest);
        profileDbo.setZip(this.zip);
        profileDbo.setCity(this.city);
    }
}
