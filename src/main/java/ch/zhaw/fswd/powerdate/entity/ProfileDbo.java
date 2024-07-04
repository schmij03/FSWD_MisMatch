package ch.zhaw.fswd.powerdate.entity;

import java.util.List;

import ch.zhaw.fswd.powerdate.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class ProfileDbo extends BaseDbo {
    private String displayName;
    private String userIntro;
    private String email;
    private String mobile;
    private LocalDate birthdate;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @ManyToMany
    @JoinTable(
        name = "profile_interests",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    private List<InterestDbo> interests;
    private String personalityTraits;
    private String lifestyle;
    @Enumerated(EnumType.STRING)
    private Gender genderInterest;
    private Short zip;
    private String city;

    @Lob
    @Column(name = "RAW_PNG_IMAGE_DATA", columnDefinition = "BLOB")
    private String rawPNGImageData;
}
