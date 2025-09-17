CREATE TABLE players (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    image_url VARCHAR(255),
    image_public_id VARCHAR(255),
    rating INT DEFAULT 1200,
    status VARCHAR(20) DEFAULT 'OFFLINE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_online TIMESTAMP
);

CREATE TABLE games (
    id BIGSERIAL PRIMARY KEY,
    white_player_id BIGINT NOT NULL,
    black_player_id BIGINT NULL,
    current_player_id BIGINT,
    status VARCHAR(20) NOT NULL,
    time_control VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    finished_at TIMESTAMP,
    fen TEXT,
    pgn TEXT,

    CONSTRAINT fk_white_player FOREIGN KEY (white_player_id) REFERENCES players (id),
    CONSTRAINT fk_black_player FOREIGN KEY (black_player_id) REFERENCES players (id),
    CONSTRAINT fk_current_player FOREIGN KEY (current_player_id) REFERENCES players (id),
    CONSTRAINT chk_different_players CHECK (white_player_id <> black_player_id)
);

CREATE TABLE moves (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    move_number INT NOT NULL,
    from_square VARCHAR(5) NOT NULL,
    to_square VARCHAR(5) NOT NULL,
    piece VARCHAR(10),
    san VARCHAR(10),
    fen TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES games (id) ON DELETE CASCADE,
    CONSTRAINT fk_player FOREIGN KEY (player_id) REFERENCES players (id)
);