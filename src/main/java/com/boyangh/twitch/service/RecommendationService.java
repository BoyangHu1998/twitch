package com.boyangh.twitch.service;

import com.boyangh.twitch.dao.FavoriteDao;
import com.boyangh.twitch.entity.db.Item;
import com.boyangh.twitch.entity.db.ItemType;
import com.boyangh.twitch.entity.response.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private static final int DEFAULT_GAME_LIMIT = 3;
    private static final int DEFAULT_PER_GAME_RECOMMENDATION_LIMIT = 10;
    private static final int DEFAULT_TOTAL_RECOMMENDATION_LIMIT = 20;

    @Autowired
    private GameService gameService;

    @Autowired
    private FavoriteDao favoriteDao;

    // Return a list of Item objects for the given type. Types are one of [Stream, Video, Clip].
    // Add items that are related to the top games provided in the argument
    private List<Item> recommendByTopGames(ItemType type, List<Game> topGames) throws RecommendationException {
        List<Item> recommendedItems = new ArrayList<>();

        for (Game game : topGames) {
            List<Item> items;
            try {
                items = gameService.searchByType(game.getId(), type, DEFAULT_PER_GAME_RECOMMENDATION_LIMIT);
            } catch (TwitchException e) {
                throw new RecommendationException("Failed to get recommendation result");
            }

            for (Item item : items) {
                if (recommendedItems.size() == DEFAULT_TOTAL_RECOMMENDATION_LIMIT) {
                    return recommendedItems;
                }
                recommendedItems.add(item);
            }
        }
        return recommendedItems;
    }


    private List<Item> recommendByFavoriteHistory(Set<String> favoritedItemIds, List<String> favoriteGameIds, ItemType type) throws RecommendationException {
        // Count the favorite game IDs from the database for the given user
        Map<String, Long> favoriteGameIdByCount = favoriteGameIds.parallelStream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Sort the game IDs by count
        List<Map.Entry<String, Long>> sortedFavoriteGameIdListByCount = new ArrayList<>(favoriteGameIdByCount.entrySet());
        sortedFavoriteGameIdListByCount.sort((Map.Entry<String, Long> e1, Map.Entry<String, Long> e2) ->
                Long.compare(e2.getValue(), e1.getValue()));

        if (sortedFavoriteGameIdListByCount.size() > DEFAULT_GAME_LIMIT) {
            sortedFavoriteGameIdListByCount = sortedFavoriteGameIdListByCount.subList(0, DEFAULT_GAME_LIMIT);
        }

        List<Item> recommendedItems = new ArrayList<>();

        // Search Twitch based on the favorite game IDs returned in the last step
        for (Map.Entry<String, Long> favoriteGame : sortedFavoriteGameIdListByCount) {
            List<Item> items;
            try {
                items = gameService.searchByType(favoriteGame.getKey(), type, DEFAULT_PER_GAME_RECOMMENDATION_LIMIT);
            } catch (TwitchException e) {
                throw new RecommendationException("Failed to get recommendation result");
            }

            for (Item item : items) {
                if (recommendedItems.size() == DEFAULT_TOTAL_RECOMMENDATION_LIMIT) {
                    return recommendedItems;
                }
                if (!favoritedItemIds.contains(item.getId())) {
                    recommendedItems.add(item);
                }
            }
        }
        return recommendedItems;
    }

    // Return a map of Item objects as the recommendation result.
    // Keys of the map are [Stream, Video, Clip]. Each key is corresponding
    // to a list of Items objects, each item object is a recommended item
    // based on the previous favorite records by the user.

    public Map<String, List<Item>> recommendItemsByUser(String userId)
            throws RecommendationException {

        Map<String, List<Item>> recommendedItemMap = new HashMap<>();
        Set<String> favoritedItemIds;
        Map<String, List<String>> favoriteGameIds;

        favoritedItemIds = favoriteDao.getFavoriteItemIds(userId);
        favoriteGameIds = favoriteDao.getFavoriteGameIds(favoritedItemIds);

        for (Map.Entry<String, List<String>> entry : favoriteGameIds.entrySet()) {
            if (entry.getValue().isEmpty()) {
                List<Game> topGames;
                try {
                    topGames = gameService.topGames(DEFAULT_GAME_LIMIT);
                } catch (TwitchException e) {
                    throw new RecommendationException("Failed to get game data for recommendation");
                }
                recommendedItemMap.put(entry.getKey(), recommendByTopGames(ItemType.valueOf(entry.getKey()), topGames));
            } else {
                recommendedItemMap.put(entry.getKey(),
                        recommendByFavoriteHistory(favoritedItemIds, entry.getValue(), ItemType.valueOf(entry.getKey())));
            }
        }

        return recommendedItemMap;
    }

    // Return a map of Item objects as the recommendation result.
    // Keys of the map are [Stream, Video, Clip]. Each key is corresponding to a
    // list of Items objects, each item object is a recommended item based on
    // the top games currently on Twitch.

    public Map<String, List<Item>> recommendItemsByDefault()
            throws RecommendationException {

        Map<String, List<Item>> recommendedItemMap = new HashMap<>();
        List<Game> topGames;

        try {
            topGames = gameService.topGames(DEFAULT_GAME_LIMIT);
        } catch (TwitchException e) {
            throw new RecommendationException("Failed to get game data for recommendation");
        }

        for (ItemType type : ItemType.values()) {
            recommendedItemMap.put(type.toString(), recommendByTopGames(type, topGames));
        }

        return recommendedItemMap;
    }

}
