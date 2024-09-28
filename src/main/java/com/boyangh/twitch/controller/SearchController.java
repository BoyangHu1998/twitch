package com.boyangh.twitch.controller;

import com.boyangh.twitch.entity.db.Item;
import com.boyangh.twitch.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    @Autowired
    private GameService gameService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<Item>> search(@RequestParam(value = "game_id") String gameId) {
        return gameService.searchItems(gameId);
    }
}