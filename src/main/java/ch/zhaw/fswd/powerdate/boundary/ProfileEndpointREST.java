package ch.zhaw.fswd.powerdate.boundary;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import ch.zhaw.fswd.powerdate.dto.RegisterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.fswd.powerdate.controller.ProfileController;
import ch.zhaw.fswd.powerdate.dto.ProfileDto;

@RestController
@RequestMapping("/api/profile")
public class ProfileEndpointREST {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final ProfileController profileController;

    @Autowired
    public ProfileEndpointREST(ProfileController profileController) {
        this.profileController = profileController;
    }

    @GetMapping
    public List<ProfileDto> getAllProfiles() {
        return profileController.getPublicProfiles();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable UUID uuid) {
        try {
            return ResponseEntity.ok().body(profileController.getPublicProfile(uuid));
        } catch (Exception e) {
            logger.warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileDto> getMyProfile(Principal principal) {
        try {
            return ResponseEntity.ok().body(profileController.getProfileByLoginName(principal.getName()));
        } catch (Exception e) {
            logger.warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/me")
    public void updateMyProfile(Principal principal, @RequestBody ProfileDto profile) {
        profileController.updateMyProfile(principal.getName(), profile);
    }

    @GetMapping("{uuid}/profileImage")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable String uuid) {
        try {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.IMAGE_PNG);
            return ResponseEntity.ok().headers(header).body(profileController.getPNGProfileImage(uuid));
        } catch (Exception e) {
            logger.warning(Arrays.toString(e.getStackTrace()));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterDto registerDto) {
        profileController.registerProfile(registerDto);
        return ResponseEntity.ok().build();
    }
}
