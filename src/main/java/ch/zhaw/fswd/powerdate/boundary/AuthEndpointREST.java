package ch.zhaw.fswd.powerdate.boundary;

import java.security.Principal;

import ch.zhaw.fswd.powerdate.entity.enums.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;

@RestController
public class AuthEndpointREST {
    @GetMapping("/auth/current")
    @RolesAllowed({Role.ADMIN, Role.USER})
    public String get(Principal principal) {
        return principal.getName();
    }
}
