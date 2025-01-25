package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    private String gameId;
    private int width;
    private int height;
    private int minesCount;
    private boolean completed;
    private String[][] field; // поле для отображения
    private boolean[][] mines; // внутреннее представление мин
    private boolean[][] revealed; // открытые ячейки
    private boolean gameOver;
}
