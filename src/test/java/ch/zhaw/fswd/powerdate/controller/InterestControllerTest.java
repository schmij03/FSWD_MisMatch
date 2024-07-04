package ch.zhaw.fswd.powerdate.controller;

import ch.zhaw.fswd.powerdate.dto.InterestDto;
import ch.zhaw.fswd.powerdate.entity.InterestDbo;
import ch.zhaw.fswd.powerdate.repository.InterestRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

class InterestControllerTest {
    @Mock
    private InterestRepository interestRepository;

    @InjectMocks
    private InterestController interestController;

    private EasyRandom generator;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        generator = new EasyRandom();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetPublicProfiles() {
        InterestDbo interestDbo1 = generator.nextObject(InterestDbo.class);
        InterestDbo interestDbo2 = generator.nextObject(InterestDbo.class);
        Mockito.when(interestRepository.findAll()).thenReturn(Arrays.asList(interestDbo1, interestDbo2));

        List<InterestDto> result = interestController.findAll();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(interestRepository).findAll();
    }
}