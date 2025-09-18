package com.chess.game.presentation.rest;

import com.chess.game.config.jwt.JwtUtil;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.application.service.interfaces.IPlayerService;
import com.chess.game.application.dto.player.AuthResponse;
import com.chess.game.application.dto.player.CreatePlayerDTO;
import com.chess.game.application.dto.player.LoginPlayerDTO;
import com.chess.game.application.dto.player.PlayerResponseDTO;
import com.chess.game.util.exception.IllegalStateExceptionCustom;
import com.chess.game.util.exception.ResourceNotFoundException;
import com.chess.game.util.mapper.PlayerMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IPlayerService service;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final PlayerMapper playerMapper;

    public AuthController(IPlayerService service,
                          AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          PlayerMapper playerMapper) {
        this.service = service;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.playerMapper = playerMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginPlayerDTO request) {
        PlayerEntity player = service.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + request.getEmail()));

        try{
            var auth = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            authManager.authenticate(auth);

            String jwt = jwtUtil.generateToken(player);
            PlayerResponseDTO playerResponse = playerMapper.mapTo(player);

            return ResponseEntity.ok(new AuthResponse(jwt, playerResponse));
        } catch (AuthenticationException e){
            throw new IllegalStateExceptionCustom("Ilegal credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody CreatePlayerDTO dto) {
        Optional<PlayerEntity> existing = service.findByEmail(dto.getEmail());

        if (existing.isPresent()) {
            try {
                PlayerEntity player = existing.get();
                var auth = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
                authManager.authenticate(auth);

                String jwt = jwtUtil.generateToken(player);
                PlayerResponseDTO playerResponse = playerMapper.mapTo(player);

                return ResponseEntity.ok(new AuthResponse(jwt, playerResponse));
            } catch (AuthenticationException e){
                throw new IllegalStateExceptionCustom("Error in autentication: " + e.getMessage());
            }

        }

        PlayerEntity created = service.save(dto);
        String jwt = jwtUtil.generateToken(created);
        PlayerResponseDTO playerResponse = playerMapper.mapTo(created);

        return ResponseEntity.ok(new AuthResponse(jwt, playerResponse));
    }
}
