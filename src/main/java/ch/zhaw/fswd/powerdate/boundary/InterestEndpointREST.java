package ch.zhaw.fswd.powerdate.boundary;

import ch.zhaw.fswd.powerdate.controller.InterestController;
import ch.zhaw.fswd.powerdate.dto.InterestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/interest")
public class InterestEndpointREST {
    private final InterestController interestController;

    @Autowired
    public InterestEndpointREST(InterestController interestController) {
        this.interestController = interestController;
    }

    @GetMapping("/all")
    public ResponseEntity<List<InterestDto>> findAll() {
        return ResponseEntity.ok().body(interestController.findAll());
    }
}
