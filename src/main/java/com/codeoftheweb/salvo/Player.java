package com.codeoftheweb.salvo;

/*import com.fasterxml.jackson.annotation.JsonIgnore;*/
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long player_id;
    private String userName;
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Player() {}

    public Player(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getPlayerId() {
        return player_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> games_player;

    public void addGamePlayer(GamePlayer game_player) {
        game_player.setPlayer(this);
        games_player.add(game_player);
    }

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> scores;

    public void addScore(Score score) {
        score.setPlayer(this);
        scores.add(score);
    }
    @JsonIgnore
    public Set<Score> getScores() {
        return scores;
    }

    public Double getScore(Game game) {
        Double score;
        List<Score> scoreFilter2 = scores.stream()
                .filter(scoreTest -> scoreTest.getPlayer().equals(this)
                        && scoreTest.getGame().equals(game))
                .collect(Collectors.toList());
        if(scoreFilter2.size() < 1) {
            score = null;
        } else {
            score = scoreFilter2.get(0).getScore();
        }
        return score;
    }

    @JsonIgnore
    public List<Game> getGames() {
        return games_player.stream().map(sub -> sub.getGame()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Player{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
