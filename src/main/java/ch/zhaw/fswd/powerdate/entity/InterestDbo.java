package ch.zhaw.fswd.powerdate.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
public class InterestDbo {
    @Id
    private Long id;
    private String name;
    private String examples;

    @ManyToMany(mappedBy = "interests")
    private List<ProfileDbo> matchProfiles;
}
