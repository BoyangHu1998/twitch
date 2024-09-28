package com.boyangh.twitch.entity.response.controller;

import com.boyangh.twitch.service.GameService;
import com.boyangh.twitch.service.TwitchException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    // Constructor-based Dependency Injection (commented out)
    // @Autowired
    // public GameController(GameService gameService) {
    //     this.gameService = gameService;
    // }

    @RequestMapping(value = "/game", method = RequestMethod.GET)
    public void getGame(@RequestParam(value = "game_name", required = false) String gameName,
                        HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");

        try {
            if (gameName != null) {
                response.getWriter().print(new ObjectMapper().writeValueAsString(gameService.searchGame(gameName)));
            } else {
                response.getWriter().print(new ObjectMapper().writeValueAsString(gameService.topGames(0)));
            }
        } catch (TwitchException e) {
            throw new ServletException(e);
        }

    }
}
