package com.example.demo.mapper;

import com.example.demo.dto.GameResponse;
import com.example.demo.model.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(source = "game_id", target = "gameId")
    @Mapping(source = "mines_count", target = "minesCount")
    Game toGame(GameResponse gameResponse);

    @Mapping(source = "gameId", target = "game_id")
    @Mapping(source = "minesCount", target = "mines_count")
    GameResponse toGameResponse(Game game);
}

