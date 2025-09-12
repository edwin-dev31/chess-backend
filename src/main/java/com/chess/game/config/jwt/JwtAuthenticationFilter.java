package com.chess.game.config.jwt;

import com.chess.game.util.exception.InvalidJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain)
		throws ServletException, IOException {
		final String requestURI = request.getRequestURI();
		System.out.println("JWT FILTER checking request: " + requestURI);

		if (requestURI.contains("/auth/")) {
			filterChain.doFilter(request, response);
            return;
		}

		final String authHeader = request.getHeader("Authorization");
		String email = null;
		String jwt = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			jwt = authHeader.substring(7);
			if (jwt.isEmpty()) {
				filterChain.doFilter(request, response);
                return;
			}
			            try {
			                email = jwtUtil.extractEmail(jwt);
			            } catch (io.jsonwebtoken.JwtException e) {
			                throw new InvalidJwtException("Invalid JWT Token: " + e.getMessage());
			            }		}

		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			var userDetails = userDetailsService.loadUserByUsername(email);
			if (jwtUtil.validateToken(jwt)) {
				var authToken = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(
					new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		filterChain.doFilter(request, response);
	}
}
