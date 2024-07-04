package ch.zhaw.fswd.powerdate.controller;

import ch.zhaw.fswd.powerdate.dto.InterestDto;
import ch.zhaw.fswd.powerdate.entity.InterestDbo;
import ch.zhaw.fswd.powerdate.repository.InterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestController {
    private final InterestRepository interestRepository;

    @Autowired
    public InterestController(InterestRepository interestRepository) {
        this.interestRepository = interestRepository;
    }

    public List<InterestDto> findAll() {
        List<InterestDbo> dbos = interestRepository.findAll();
        return dbos.stream().map(dbo -> new InterestDto().fromDbo(dbo)).toList();
    }
}
