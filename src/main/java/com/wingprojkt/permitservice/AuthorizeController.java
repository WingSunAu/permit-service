package com.wingprojkt.permitservice;

import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizeController {

    private static final Map<String, Set<String>> ROLE_PERMS = Map.of(
        "ADMIN",  Set.of("READ", "WRITE", "DELETE"),
        "EDITOR", Set.of("READ", "WRITE"),
        "VIEWER", Set.of("READ"));

    public record AuthRequest(String role, String action) {}
    public record AuthResponse(boolean allowed, String reason) {}

@PostMapping("/authorize")
public AuthResponse authorize(@RequestBody AuthRequest req) {
    if (req.role() == null || req.action() == null) {
        return new AuthResponse(false, "denied by default");
    }
    var perms = ROLE_PERMS.getOrDefault(req.role(), Set.of());
    boolean ok = perms.contains(req.action());
    return new AuthResponse(ok, ok ? "role grants action" : "denied by default");
}
}