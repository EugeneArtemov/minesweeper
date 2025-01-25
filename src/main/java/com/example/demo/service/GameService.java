package com.example.demo.service;

import com.example.demo.dto.GameResponse;
import com.example.demo.mapper.GameMapper;
import com.example.demo.model.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {
    private final Map<String, Game> games = new HashMap<>();
    private final GameMapper gameMapper;

    public GameResponse createGame(GameResponse gameResponse) {
        Game game = gameMapper.toGame(gameResponse);
        int width = game.getWidth();
        int height = game.getHeight();
        int minesCount = game.getMinesCount();

        validateInput(width, height, minesCount);

        String gameId = UUID.randomUUID().toString();
        String[][] field = new String[height][width];
        boolean[][] mines = new boolean[height][width];
        boolean[][] revealed = new boolean[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                field[i][j] = " ";
                revealed[i][j] = false;
            }
        }

        placeMines(mines, width, height, minesCount);

        Game createdGame = new Game(gameId, width, height, minesCount, false, field, mines, revealed, false);
        games.put(gameId, createdGame);
        return gameMapper.toGameResponse(createdGame);
    }

    public GameResponse makeTurn(String gameId, int row, int col) {
        Game game = getGame(gameId);
        if (game.isCompleted()) {
            throw new IllegalStateException("Игра уже завершена.");
        }

        if (row < 0 || row >= game.getHeight() || col < 0 || col >= game.getWidth()) {
            throw new IllegalArgumentException("Координаты вне допустимого диапазона.");
        }

        if (game.getRevealed()[row][col]) {
            throw new IllegalArgumentException("Ячейка уже открыта.");
        }

        // Если это минное поле, помечаем все мины как "X"
        if (game.getMines()[row][col]) {
            revealAllMines(game); // Все мины помечаются как "X"
            game.setCompleted(true);
            game.setGameOver(true);
            return gameMapper.toGameResponse(game);
        }

        // Если это безопасная ячейка, открываем её и
        // Заменяем revealCell на рекурсивное раскрытие
        revealCells(game, row, col);

        // После раскрытия проверяем, не выиграл ли игрок
        checkForWin(game);

        GameResponse gameResponse = gameMapper.toGameResponse(game);

        return gameResponse;
    }





    private void revealCells(Game game, int row, int col) {
        // Проверка выхода за пределы поля
        if (row < 0 || row >= game.getHeight() || col < 0 || col >= game.getWidth()) {
            return;
        }

        // Если ячейка уже раскрыта, выходим
        if (game.getRevealed()[row][col]) {
            return;
        }

        String minesAround = getMinesAround(game, row, col);
        game.getField()[row][col] = minesAround;
        game.getRevealed()[row][col] = true;

        // Если вокруг ячейки нет мин, то есть "0", то рекурсивно раскрываем соседние ячейки
        if (minesAround.equals("0")) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        revealCells(game, row + i, col + j); // Рекурсивно открываем соседние ячейки
                    }
                }
            }
        }
    }

    // Считаем количество мин вокруг текущей ячейки
    private String getMinesAround(Game game, int row, int col) {
        int minesCount = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                // Проверяем соседние ячейки, если они находятся внутри поля и содержат мину
                if (newRow >= 0 && newRow < game.getHeight() && newCol >= 0 && newCol < game.getWidth()) {
                    if (game.getMines()[newRow][newCol]) {
                        minesCount++;
                    }
                }
            }
        }

        return String.valueOf(minesCount);
    }

    private void placeMines(boolean[][] mines, int width, int height, int minesCount) {
        Random random = new Random();
        int placed = 0;

        while (placed < minesCount) {
            int row = random.nextInt(height);
            int col = random.nextInt(width);
            if (!mines[row][col]) {
                mines[row][col] = true;
                placed++;
            }
        }
    }

    private int countSurroundingMines(boolean[][] mines, int row, int col) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = row + dr, nc = col + dc;
                if (nr >= 0 && nr < mines.length && nc >= 0 && nc < mines[0].length && mines[nr][nc]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void revealAllMines(Game game) {
        for (int i = 0; i < game.getHeight(); i++) {
            for (int j = 0; j < game.getWidth(); j++) {
                if (game.getMines()[i][j]) {
                    game.getField()[i][j] = "X"; // Все мины становятся "X", если игрок проиграл
                } else {
                    // Для остальных ячеек показываем количество мин рядом
                    if (!game.getRevealed()[i][j]) {
                        game.getField()[i][j] = String.valueOf(countSurroundingMines(game.getMines(), i, j));
                    }
                }
            }
        }
    }

    private void checkForWin(Game game) {
        for (int i = 0; i < game.getHeight(); i++) {
            for (int j = 0; j < game.getWidth(); j++) {
                // Если есть закрытая безопасная ячейка, игра ещё не завершена
                if (!game.getRevealed()[i][j] && !game.getMines()[i][j]) {
                    return;
                }
            }
        }

        // Игра завершена победой: все безопасные ячейки открыты
        game.setCompleted(true);

        // Помечаем все мины как "M"
        for (int i = 0; i < game.getHeight(); i++) {
            for (int j = 0; j < game.getWidth(); j++) {
                if (game.getMines()[i][j]) {
                    game.getField()[i][j] = "M";
                }
            }
        }
    }

    private void validateInput(int width, int height, int minesCount) {
        if (width <= 0 || height <= 0 || width > 30 || height > 30) {
            throw new IllegalArgumentException("Размеры поля должны быть от 1 до 30.");
        }
        if (minesCount <= 0 || minesCount >= width * height) {
            throw new IllegalArgumentException("Количество мин должно быть больше 0 и меньше количества ячеек.");
        }
    }

    private Game getGame(String gameId) {
        if (!games.containsKey(gameId)) {
            throw new IllegalArgumentException("Игра с таким ID не найдена.");
        }
        return games.get(gameId);
    }

}
