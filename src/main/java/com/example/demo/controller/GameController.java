package com.example.demo.controller;

import com.example.demo.dto.GameResponse;
import com.example.demo.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@Slf4j
@RequestMapping("/api/minesweeper")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    //@Operation(summary = "Метод для начала новой игры")
    @PostMapping("/new")
    public ResponseEntity<GameResponse> createGame(@RequestBody GameResponse request) {
        log.debug("POST-request, createGame - start, dto = {}", request);
        GameResponse game = gameService.createGame(request);
        log.debug("POST-request, createGame - end, response = {}", game);
        return ResponseEntity.ok(game);
    }

    //@Operation(summary = "Метод для хода пользователя")
    @PostMapping("/turn")
    public ResponseEntity<GameResponse> makeTurn(@RequestBody Map<String, Object> request) {
        log.debug("POST-request, makeTurn - start, dto = {}", request);
        String gameId = (String) request.get("game_id");
        int row = (int) request.get("row");
        int col = (int) request.get("col");
        GameResponse game = gameService.makeTurn(gameId, row, col);
        log.debug("POST-request, makeTurn - end, response = {}", game);
        return ResponseEntity.ok(game);
    }


}

