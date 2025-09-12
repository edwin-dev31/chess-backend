package com.chess.game.config.jwt;

import com.chess.game.persistence.entity.PlayerEntity;
import com.chess.game.persistence.repository.IPlayerRepository;
import com.chess.game.util.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetails implements UserDetailsService {

	private final IPlayerRepository playerRepository;

	@Autowired
	public CustomUserDetails(IPlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		System.out.println("Searching user in DB: " + email);

		PlayerEntity playerEntity = playerRepository.findByEmail(email)
			.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

		return User.withUsername(playerEntity.getEmail())
			.password(playerEntity.getPassword())
			.roles("USER")
			.build();
	}
}
