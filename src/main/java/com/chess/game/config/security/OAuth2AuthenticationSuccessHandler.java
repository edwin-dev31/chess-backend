package com.chess.game.config.security;

import com.chess.game.config.jwt.JwtUtil;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.application.service.impl.PlayerService;
import com.chess.game.application.dto.player.CreatePlayerDTO;
import com.chess.game.util.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.chess.game.util.AppRoutes.FRONTEND_REDIRECTION_URL;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final PlayerService userService;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil, @Lazy PlayerService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        String email = null;
        String username = null;
        String imageUrl = null;

        if (registrationId.equals("google")) {
            email = oAuth2User.getAttribute("email");
            username = oAuth2User.getAttribute("name");
            imageUrl = oAuth2User.getAttribute("picture");
        }

        if (email == null || username == null) {
            throw new IllegalStateException("Missing user info from provider: " + registrationId);
        }

        if (userService.findByEmail(email).isEmpty()) {
            Optional<PlayerEntity> player = userService.findByUsername(username);
            if(!player.isEmpty()){
                Random random = new Random();
                int numero = 1000 + random.nextInt(9000);
                username += numero;
            }
            CreatePlayerDTO dto = new CreatePlayerDTO();
            dto.setUsername(username);
            dto.setEmail(email);
            dto.setImageUrl(imageUrl);
            dto.setPassword(UUID.randomUUID().toString());
            dto.setRating(1200);
            userService.save(dto);
        }
        PlayerEntity player = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

        String jwt = jwtUtil.generateToken(player);
        String redirectUrl = FRONTEND_REDIRECTION_URL + jwt;

        response.sendRedirect(redirectUrl);
    }

}