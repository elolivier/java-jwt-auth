package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long game_id;
    private final long gameDate;

    public Game() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        gameDate = instant.toEpochMilli();
    }

    public long getCreationDate() {
        return gameDate;
    }

    public long getGameId() {
        return game_id;
    }

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> scores;

    public void addScore(Score score) {
        score.setGame(this);
        scores.add(score);
    }
    @JsonIgnore
    public Set<Score> getScores() {
        return scores;
    }

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public void addGamePlayer(GamePlayer game_player) {
        game_player.setGame(this);
        gamePlayers.add(game_player);
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    @JsonIgnore
    public List<Player> getPlayers() {
        return gamePlayers.stream().map(GamePlayer::getPlayer).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Game{" +
                "creationDate=" + gameDate +
                '}';
    }
}
