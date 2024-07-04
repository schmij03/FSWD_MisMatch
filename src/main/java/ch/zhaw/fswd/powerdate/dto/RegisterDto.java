package ch.zhaw.fswd.powerdate.dto;

import ch.zhaw.fswd.powerdate.entity.InterestDbo;
import ch.zhaw.fswd.powerdate.entity.ProfileDbo;
import ch.zhaw.fswd.powerdate.entity.UserDbo;
import ch.zhaw.fswd.powerdate.entity.enums.Gender;
import ch.zhaw.fswd.powerdate.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    private String displayName;
    private String loginName;
    private String passwordHash;
    private String userIntro;
    private List<InterestDto> interests;
    private String email;
    private String mobile;
    private LocalDate birthdate;
    private Gender gender;
    private Gender genderInterest;
    private Short zip;
    private String city;
    private String rawPNGImageData;

    public ProfileDbo getProfileDbo(List<InterestDbo> interests) {
        ProfileDbo dbo = new ProfileDbo();
        dbo.setDisplayName(this.displayName);
        dbo.setUserIntro(this.userIntro);
        dbo.setEmail(this.email);
        dbo.setMobile(this.mobile);
        dbo.setBirthdate(this.birthdate);
        dbo.setGender(this.gender);
        dbo.setGenderInterest(this.genderInterest);
        dbo.setInterests(interests);
        dbo.setZip(this.zip);
        dbo.setCity(this.city);
        dbo.setRawPNGImageData(this.rawPNGImageData);
        return dbo;
    }

    public UserDbo getUserDbo(ProfileDbo profileDbo) {
        UserDbo dbo = new UserDbo();
        dbo.setLoginName(this.loginName);
        dbo.setPasswordHash(this.passwordHash);
        dbo.setRoleName(Role.USER_ROLE);
        dbo.setProfile(profileDbo);
        return dbo;
    }
}
