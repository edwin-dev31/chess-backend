## 🔐 Autenticación y Usuarios (REST)
Estos endpoints son síncronos y se manejan mejor con REST:
- [x] POST /auth/register → Registro de usuario (username, password) y devuelve token.
- [x] POST /auth/login → Login de usuario, devuelve token.
- [ ] GET /users/online → Lista de usuarios conectados.
- [ ] GET /users/{id}/profile → Ver perfil de un usuario (numero de victorias, derrotas y empates).

## 🎮 Gestión de Partidas (REST)
Endpoints que inicializan o configuran partidas:

- [ ] POST /games → Crear nueva partida.
- [ ] POST /games/{id}/invite → Invitar a un usuario.
- [ ] POST /games/{id}/accept → Aceptar invitación.
- [ ] POST /games/{id}/reject → Rechazar invitación.
- [ ] POST /games/{id}/resign → Abandonar partida → victoria rival.
- [ ] GET /games/{id}/status → Estado actual del juego.
- [ ] GET /games/{id}/history → Movimientos realizados.

⚡ Comunicación en Tiempo Real (WebSockets)
Los sockets garantizan la actualización inmediata del tablero y eventos de juego:

Conexión

- [ ] connect → Cliente se conecta al servidor con token.
- [ ] disconnect → Cliente se desconecta.

Lobby / Invitaciones
- [ ] invite_sent → Notifica al rival sobre invitación.
- [ ] invite_accepted → Rival aceptó invitación.
- [ ] invite_rejected → Rival rechazó invitación.

Juego
- [ ] game_start → Inicia partida, se envía estado inicial (tablero, turno).
- [ ] move_played → Jugador realiza movimiento { from, to, piece, promotion? }.
- [ ] board_update → Actualización del tablero para ambos jugadores.
- [ ] illegal_move → Notificación de movimiento inválido.
- [ ] check / checkmate / stalemate → Estados especiales.
- [ ] game_over → Resultado final (victoria, tablas, abandono).
- [ ] resign → Notificación de que un jugador se rinde.
- [ ] player_disconnected → Notificación si un jugador se cae (posible reconexión).
- [ ] player_reconnected → Retoma la partida en curso.

Chesslib para manejar el juego en vivo.
PGN4Java para importar/exportar partidas.