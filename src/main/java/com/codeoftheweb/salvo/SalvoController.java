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
        dto.put("score", game_player.getScore());
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
        dto.put("salvos", getTurnsList(requesterGp, thisGame.getGamePlayers(), opponent));
        return dto;
    }

    @Autowired
    ScoreRepository repoScore;

    private int getStateOfGame(GamePlayer gpRequester, GamePlayer opponent) {
        int stateOfGame;
        //State 1: Place your ships
        if(gpRequester.getShips().size() == 0) {
            stateOfGame = 1;
        }
        //State 2: Waiting for opponent
        else if(opponent == null) {
            stateOfGame = 2;
        }
        //State 3: Waiting for opponent place ships
        else if(opponent.getShips().size() == 0) {
            stateOfGame = 3;
        }
        //State 6: All your ships sunk (you lose)
        else if(allSunk(gpRequester)) {
            stateOfGame = 6;
            Score testLoser = repoScore.findByGameAndPlayer(gpRequester.getGame(), gpRequester.getPlayer());
            Score loser = new Score(gpRequester.getGame(), gpRequester.getPlayer(), 0.0);
            if(testLoser == null) {
                repoScore.save(loser);
            }
        }
        //State 7: All your enemy ships sunk (you win)
        else if(allSunk(opponent)) {
            stateOfGame = 7;
            Score test = repoScore.findByGameAndPlayer(gpRequester.getGame(), gpRequester.getPlayer());
            Score winner = new Score(gpRequester.getGame(), gpRequester.getPlayer(), 1.0);
            if(test == null) {
                repoScore.save(winner);
            }
        }

        //State 4-5: Waiting for opponent to shoot or shoot
        else if(gpRequester.getGamePlayerId() < opponent.getGamePlayerId()) {
            if(gpRequester.salvos.size() > opponent.salvos.size()) {
                //State 4: Waiting for opponent to shoot
                stateOfGame = 4;
            }else {
                //State 5: Shoot your salvo
                stateOfGame = 5;
            }
        }
        //State 4-5: Waiting for opponent to shoot or shoot
        else if(gpRequester.getGamePlayerId() > opponent.getGamePlayerId()) {
            if(gpRequester.salvos.size() == opponent.salvos.size()) {
                //State 4: Waiting for opponent to shoot
                stateOfGame = 4;
            }else {
                //State 5: Shoot your salvo
                stateOfGame = 5;
            }
        }

        else {
            stateOfGame = 9;
        }
        return stateOfGame;
    }

    private boolean allSunk(GamePlayer gp) {
        boolean gameOver;
        int ships=0;
        for (Ship ship : gp.getShips()) {
            boolean sunk = isSink(gp, ship);
            if (sunk) {
                ships++;
            }
        }
        if(ships == gp.getShips().size()) {
            gameOver = true;
        }else {
            gameOver = false;
        }
        return gameOver;
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

    private List<Object> getTurnsList(GamePlayer requesterGp, Set<GamePlayer> bothGps, GamePlayer opponent) {
        List<Object> turnList = new LinkedList<>();
        Long turns = (long) getTurnsQuantity(bothGps);
        //System.out.println("quantity of turns " + turns);
        for (Long i = 1L; i <= turns; i++) {
            turnList.add(getMapPlayers(i,requesterGp));
            if (opponent != null){
                turnList.add(getMapPlayers(i,opponent));
            }
        }
        return turnList;
    }

    private Map<String, Object> getMapPlayers(Long turn, GamePlayer gamePlayer) {
        Map<String, Object> playerPerTurn = new LinkedHashMap<>();
        playerPerTurn.put("turn", turn);
        playerPerTurn.put("player", gamePlayer.getPlayer().getPlayerId());
        playerPerTurn.put("salvo",getSalvoInfo(turn, gamePlayer));
        return playerPerTurn;
    }

    private Map<String, Object> getSalvoInfo(Long turn, GamePlayer gamePlayer) {
        Map<String, Object> eachPlayerSalvoPerTurn = new LinkedHashMap<>();
        eachPlayerSalvoPerTurn.put("locations", getSalvoInformation(turn, gamePlayer));
        eachPlayerSalvoPerTurn.put("shipsStatus", getShipsStatus(turn, gamePlayer));
        return eachPlayerSalvoPerTurn;
    }

    private List<Map<String, Object>> getShipsStatus(Long turn, GamePlayer gamePlayer) {
        List<Map<String, Object>> allShips = new ArrayList<>();
        gamePlayer.getShips().forEach((ship) -> {
            Map<String, Object> eachShip = new LinkedHashMap<>();
            eachShip.put("ship", ship.getShipType());
            eachShip.put("cellsHitted", getHitStatus(turn, gamePlayer, ship));
            eachShip.put("shipSink", isSink(gamePlayer, ship));
            allShips.add(eachShip);
        });
        return allShips;
    }

    private List<String> getHitStatus(Long turn, GamePlayer gamePlayer, Ship ship) {
        List<String> hits = new ArrayList<>();
        GamePlayer opponent = getOpponent(gamePlayer.getGame().getGamePlayers(),gamePlayer.getGamePlayerId());
        List<String> opponentSalvo = getSalvoLocations(turn, opponent);
        if (opponentSalvo != null && opponent != gamePlayer) {
            opponentSalvo.forEach((shoot)-> {
                ship.getLocations().forEach((position)-> {
                    if (position == shoot) {
                        hits.add(position);
                    }
                });
            });
        }
        return hits;
    }

    private boolean isSink(GamePlayer gamePlayer, Ship ship) {
        boolean sunk;
        GamePlayer opponent = getOpponent(gamePlayer.getGame().getGamePlayers(), gamePlayer.getGamePlayerId());
        Set<Salvo> opponentSalvo = opponent.getSalvos();
        List<String> shipPositions = ship.getLocations();
        List<String> allShoots = opponentSalvo.stream().map(salvo -> salvo.getSalvo_locations()).flatMap(shoot -> shoot.stream()).collect(Collectors.toList());
        sunk = allShoots.containsAll(shipPositions);
        return sunk;
    }

    private List<String> getSalvoLocations(Long turn, GamePlayer gp) {
        Predicate<Salvo> isTurn = salvo -> salvo.getTurn() == turn;
        Optional<Salvo> returnSalvo = gp.getSalvos()
                .stream()
                .filter(isTurn)
                .findAny();
        List<String> locations;
        try {
            locations = returnSalvo.get().getSalvo_locations();
        } catch (Exception NoSuchElementException) {
            locations = null;
        }
        return locations;
    }

    private List<Map<String, Object>> getSalvoInformation(Long turn, GamePlayer gp) {
        List<Map<String, Object>> salvoInfo = new ArrayList<>();
        List<String> locations = getSalvoLocations(turn, gp);
        if(locations != null) {
            for (String shoot : locations) {
                Map<String, Object> eachSalvoPerTurn = new LinkedHashMap<>();
                eachSalvoPerTurn.put("cell", shoot);
                eachSalvoPerTurn.put("hit", shootHit(shoot, gp));
                salvoInfo.add(eachSalvoPerTurn);
            }
        }
        return salvoInfo;
    }

    private boolean shootHit(String shoot, GamePlayer gp) {
        boolean hit = false;
        GamePlayer opponent2 = getOpponent(gp.getGame().getGamePlayers(),gp.getGamePlayerId());
        List<String> allShipsLocations = opponent2.getShips().stream().map(ship -> ship.getLocations()).flatMap(cell->cell.stream()).collect(Collectors.toList());
        for (String cell : allShipsLocations) {
            if(shoot == cell) {
                hit = true;
            }
        }
        return hit;
    }

    private int getTurnsQuantity(Set<GamePlayer> bothGp) {
        int turns;
        turns = bothGp.stream()
                .max(Comparator.comparingInt(gp -> gp.getSalvos().size()))
                .get()
                .getSalvos()
                .size();
        return turns;
    }

    //-------------------TASK 5--------------------
    @Autowired
    PlayerRepository repoPlayer;

    @RequestMapping("/leaderboard")
    public List<Object> getApiLeaderboard() {
        return repoPlayer
                .findAll()
                .stream()
                .map(player -> boardDTO(player))
                .collect(Collectors.toList());
    }

    private Map<String, Object> boardDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("player", player.getUsername());
        dto.put("scores", getPlayerScores(player.getScores()));
        return dto;
    }

    private Map<String, Object> getPlayerScores(Set<Score> scores) {
        Map<String, Object> scoreMap = new LinkedHashMap<>();
        Predicate<Score> filterWon = score -> score.getScore().equals(1.0);
        Predicate<Score> filterTied = score -> score.getScore().equals(0.5);
        Predicate<Score> filterLost = score -> score.getScore().equals(0.0);
        scoreMap.put("won", scores.stream().filter(filterWon).count());
        scoreMap.put("lost", scores.stream().filter(filterLost).count());
        scoreMap.put("tied", scores.stream().filter(filterTied).count());
        scoreMap.put("score", (scores.stream().filter(filterWon).count())*1.0 +
                (scores.stream().filter(filterTied).count())*0.5);
        return scoreMap;
    }
    //-------------------JAVA 2 - TASK 1--------------------

}
