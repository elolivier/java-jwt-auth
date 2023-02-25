package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.Player;
import com.codeoftheweb.salvo.PlayerRepository;
import com.codeoftheweb.salvo.controllers.dto.SigInRequest;
import com.codeoftheweb.salvo.controllers.dto.SignUpRequest;
import com.codeoftheweb.salvo.utils.CustomPasswordEncoder;
import com.codeoftheweb.salvo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private CustomPasswordEncoder customPasswordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @PostMapping("login")
    public ResponseEntity<?> login (@RequestBody SigInRequest request) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(), request.getPassword()
                            )
                    );

            Player player = (Player) authenticate.getPrincipal();
            HashMap<String, Object> responseBody = new HashMap<>();
            List<String> authorities = player.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            responseBody.put("username", player.getUsername());
            responseBody.put("email", player.getEmail());
            responseBody.put("authorities", authorities);
            responseBody.put("id", player.getPlayerId());
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            jwtUtils.generateToken(player)
                    )
                    .body(responseBody);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        // Validate request data
        if (playerRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (playerRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new player's account
        Player player = new Player(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                customPasswordEncoder.getPasswordEncoder().encode(signUpRequest.getPassword())
        );

        playerRepository.save(player);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
