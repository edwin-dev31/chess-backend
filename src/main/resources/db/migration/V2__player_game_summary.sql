CREATE VIEW player_game_summary AS
SELECT
    p.id AS player_id,
    g.id AS game_id,
    CASE
        WHEN g.white_player_id = p.id THEN opp_black.username
        ELSE opp_white.username
    END AS opponent,
    CASE
        WHEN g.white_player_id = p.id THEN opp_black.rating
        ELSE opp_white.rating
    END AS opponent_rating,
    CASE
        WHEN g.status = 'FINISHED' AND g.current_player_id = p.id THEN 'win'
        WHEN g.status = 'FINISHED' AND g.current_player_id IS NULL THEN 'draw'
        WHEN g.status = 'FINISHED' THEN 'lose'
        ELSE NULL
    END AS result,
    g.finished_at::date AS date,
    g.time_control,
    COUNT(m.id) AS moves
FROM players p
JOIN games g ON p.id IN (g.white_player_id, g.black_player_id)
LEFT JOIN players opp_white ON opp_white.id = g.white_player_id
LEFT JOIN players opp_black ON opp_black.id = g.black_player_id
LEFT JOIN moves m ON m.game_id = g.id
WHERE g.status = 'FINISHED'
GROUP BY p.id, g.id, opp_white.username, opp_black.username, opp_white.rating, opp_black.rating, g.current_player_id, g.finished_at, g.time_control;
