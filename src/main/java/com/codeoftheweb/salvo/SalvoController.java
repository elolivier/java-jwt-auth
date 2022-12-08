package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @RequestMapping("/games")
    public Map<String, Object> getApiGames() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("games",getGames());
        return dto;
    }

    //return all games saved in DB
    public List<Object> getGames() {
        return gameRepository
                .findAll()
                .stream()
                .map(this::gameDTO)
                .collect(Collectors.toList());
    }

    private Map<String, Object> gameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("game_id", game.getGameId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers()
                .stream()
                .map(this::gamePlayerDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> gamePlayerDTO(GamePlayer game_player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("game_player_id", game_player.getGamePlayerId());
        dto.put("joined", game_player.getPlayerAddDate());
        dto.put("player", game_player.getPlayer());
        return dto;
    }

    @Autowired
    GamePlayerRepository gamePlayerRepository;

    @RequestMapping("/game_view/{game_player_id}")
    public ResponseEntity<Map<String, Object>> findGamePlayer(@PathVariable long game_player_id) {
        GamePlayer gamePlayerOwner = gamePlayerRepository.getReferenceById(game_player_id);
        //String requesterUserName = gamePlayerOwner.getPlayer().getUserName();
        Game game = gamePlayerOwner.getGame();
        GamePlayer opponent = getOpponent(game.getGamePlayers(), game_player_id);
        return new ResponseEntity<>(getGameInfo(game, gamePlayerOwner, opponent), HttpStatus.OK);
    }

    private Map<String, Object> getGameInfo(Game thisGame, GamePlayer requesterGp, GamePlayer opponent) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("game_id", thisGame.getGameId());
        dto.put("created", thisGame.getCreationDate());
        dto.put("gamePlayers", thisGame.getGamePlayers()
                .stream()
                .map(game_player -> gamePlayerDTO(game_player))
                .collect(Collectors.toList()));
        dto.put("ships", requesterGp.getShips()
                .stream()
                .map(ship -> shipDTO(ship))
                .collect(Collectors.toList()));
        return dto;
    }

    //This function return the GamePlayer of the opponent or null
    private GamePlayer getOpponent(Set<GamePlayer> bothGps, Long ownerGpId) {
        Predicate<GamePlayer> isOpponent = gamePlayer -> gamePlayer.getGamePlayerId() != ownerGpId;
        Optional<GamePlayer> returnGp = bothGps
                .stream()
                .filter(isOpponent)
                .findAny();
        GamePlayer opponent2 = returnGp.orElse(null);
        return opponent2;
    }

    private Map<String, Object> shipDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", ship.getShipType());
        dto.put("locations", ship.getLocations());
        return dto;
    }
}
