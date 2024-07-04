package ch.zhaw.fswd.powerdate.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserDbo {
    @Id
    private String loginName;

    private String passwordHash;

    private String roleName;

    @OneToOne
    private ProfileDbo profile;
}
