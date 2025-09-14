package com.chess.game.infrastructure.entity;

import com.chess.game.util.PlayerStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "players")
public class PlayerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(unique = true)
	private String email;

	private String imageUrl;
	private String imagePublicId;

	@ColumnDefault("1200")
	private Integer rating;

	@Enumerated(EnumType.STRING)
	@ColumnDefault("'OFFLINE'")
	private PlayerStatus status;

	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastOnline;
}
