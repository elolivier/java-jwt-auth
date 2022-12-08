package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long game_player_id;
    private long userJoinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @OneToMany(mappedBy = "gp", fetch = FetchType.EAGER)
    Set<Ship> ships;

    public GamePlayer() {}

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public GamePlayer(Game game, Player player) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        this.userJoinDate = instant.toEpochMilli();
        this.game = game;
        this.player = player;
    }

    public long getPlayerAddDate() {
        return userJoinDate;
    }

    @JsonIgnore
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public long getGamePlayerId() {
        return game_player_id;
    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "playerAddDate=" + userJoinDate +
                ", game=" + game +
                ", player=" + player +
                '}';
    }
}
