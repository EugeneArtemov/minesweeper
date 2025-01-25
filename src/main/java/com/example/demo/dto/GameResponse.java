package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameResponse {
    private String game_id;
    private int width;
    private int height;
    private int mines_count;
    private boolean completed;
    private String[][] field;
}
