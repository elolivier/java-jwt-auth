package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository) {
		return args -> {
			Player player1 = new Player("Joker", "player1@mail.com", "12345");
			Player player2 = new Player("Neo", "player2@mail.com", "56789");
			Player player3 = new Player("Frodo", "player3@mail.com", "12345");
			Player player4 = new Player("Nicola", "player4@mail.com", "56789");
			Player player5 = new Player("Isaac", "player5@mail.com", "12345");
			Game game1 = new Game();
			Game game2 = new Game();
			Game game3 = new Game();
			Game game4 = new Game();
			GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
			GamePlayer gamePlayer2 = new GamePlayer(game1, player2);
			GamePlayer gamePlayer3 = new GamePlayer(game2, player3);
			GamePlayer gamePlayer4 = new GamePlayer(game2, player4);
			GamePlayer gamePlayer5 = new GamePlayer(game3, player2);
			GamePlayer gamePlayer6 = new GamePlayer(game3, player5);
			GamePlayer gamePlayer7 = new GamePlayer(game4, player1);
			//GamePlayer gamePlayer8 = new GamePlayer(game4, player4);
			List<String> sh1_loc = new ArrayList<>(Arrays.asList("J1", "J2", "J3", "J4"));
			List<String> sh2_loc = new ArrayList<>(Arrays.asList("C2", "C3", "C4"));
			List<String> sh3_loc = new ArrayList<>(Arrays.asList("A1", "A2", "A3", "A4"));
			List<String> sh4_loc = new ArrayList<>(Arrays.asList("F2", "G2", "H2"));


			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);
			playerRepository.save(player5);
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);
			gamePlayerRepository.save(gamePlayer5);
			gamePlayerRepository.save(gamePlayer6);
			gamePlayerRepository.save(gamePlayer7);
//			gamePlayerRepository.save(gamePlayer8);

			Ship ship1 = new Ship("Battleship", sh1_loc, gamePlayer2);
			Ship ship2 = new Ship("Destroyer", sh2_loc, gamePlayer4);
			Ship ship3 = new Ship("Battleship", sh3_loc, gamePlayer5);
			Ship ship4 = new Ship("Battleship", sh3_loc, gamePlayer1);
			Ship ship5 = new Ship("Destroyer", sh2_loc, gamePlayer1);
			Ship ship6 = new Ship("Destroyer", sh4_loc, gamePlayer2);
			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
		};
	}
}
