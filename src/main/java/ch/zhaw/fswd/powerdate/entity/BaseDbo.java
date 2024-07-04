package ch.zhaw.fswd.powerdate.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseDbo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
}
