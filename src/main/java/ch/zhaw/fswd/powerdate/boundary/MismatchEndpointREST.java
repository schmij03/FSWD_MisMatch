package ch.zhaw.fswd.powerdate.boundary;

import ch.zhaw.fswd.powerdate.controller.MismatchController;
import ch.zhaw.fswd.powerdate.dto.ProfileDto;
import ch.zhaw.fswd.powerdate.entity.enums.Role;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/match")
public class MismatchEndpointREST {
    private final MismatchController mismatchController;

    @Autowired
    public MismatchEndpointREST(MismatchController mismatchController) {
        this.mismatchController = mismatchController;
    }

    @PutMapping("/createMatches")
    @RolesAllowed({Role.ADMIN})
    public ResponseEntity<Void> createMatches() {
        mismatchController.createMismatches();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getMatchee/{uuid}")
    @RolesAllowed({Role.ADMIN, Role.USER})
    public ResponseEntity<ProfileDto> getMatcheeByUuid(@PathVariable UUID uuid) {
        try {
            return ResponseEntity.ok().body(mismatchController.getPublicProfile(uuid));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getMatches")
    @RolesAllowed({Role.ADMIN, Role.USER})
    public ResponseEntity<List<UUID>> getMatches(@RequestParam UUID userUuid) {
        try {
            return ResponseEntity.ok().body(mismatchController.getMismatchesByTodayAndMismatcherId(userUuid));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
