package ch.zhaw.fswd.powerdate.boundary;

import ch.zhaw.fswd.powerdate.controller.InterestController;
import ch.zhaw.fswd.powerdate.dto.InterestDto;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;


class InterestEndpointRESTTest {
    @Mock
    private InterestController interestController;

    @InjectMocks
    private InterestEndpointREST interestEndpointREST;

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
    void getAllInterests_ShouldReturnListOfInterest() {
        // Arrange
        List<InterestDto> expectedInterests = Arrays.asList(
                generator.nextObject(InterestDto.class),
                generator.nextObject(InterestDto.class)
        );
        Mockito.when(interestController.findAll()).thenReturn(expectedInterests);

        // Act
        ResponseEntity<List<InterestDto>> response = interestEndpointREST.findAll();

        // Assert
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> Assertions.assertEquals(expectedInterests, response.getBody())
        );
    }
}