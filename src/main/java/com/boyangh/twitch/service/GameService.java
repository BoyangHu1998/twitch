package com.boyangh.twitch.service;

import com.boyangh.twitch.entity.db.Item;
import com.boyangh.twitch.entity.db.ItemType;
import com.boyangh.twitch.entity.response.Game;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.http.HttpEntity;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


// @Service Annotation: This annotation is used to mark the class as a service in the Spring context,
// indicating that it's a component that contains business logic.

@Service
public class GameService {
    private static final String TOKEN = "Bearer 63mgq60czkjnkuexif36ya99yf1fky";
    private static final String CLIENT_ID = "0ssi2isgb5ard87ip0mes2psojko0y";
    private static final String TOP_GAME_URL = "https://api.twitch.tv/helix/games/top?first=%s";
    private static final String GAME_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/games?name=%s";
    private static final int DEFAULT_GAME_LIMIT = 20;

    // Added support for stream, video, and clip search
    private static final String STREAM_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/streams?game_id=%s&first=%s";
    private static final String VIDEO_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/videos?game_id=%s&first=%s";
    private static final String CLIP_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/clips?game_id=%s&first=%s";

    private static final String TWITCH_BASE_URL = "https://www.twitch.tv/";
    private static final int DEFAULT_SEARCH_LIMIT = 20;

    // Build the request URL which will be used when calling Twitch APIs,
    // e.g., https://api.twitch.tv/helix/games/top when trying to get top games.
    private String buildGameURL(String url, String gameName, int limit) {
        if (gameName.isEmpty()) {
            return String.format(url, limit);
        } else {
            // Encode special characters in URL, e.g., Rick Sun -> Rick%20Sun
            gameName = URLEncoder.encode(gameName, StandardCharsets.UTF_8);
            return String.format(url, gameName);
        }
    }


    // Method to build search URL for Twitch API (e.g., Clips, Streams, etc.)
    private String buildSearchURL(String url, String gameId, int limit) {
        try {
            gameId = URLEncoder.encode(gameId, "UTF-8");  // Encode the gameId to handle special characters
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.format(url, gameId, limit);  // Format the URL with gameId and limit
    }

    // Similar to getGameList, convert the JSON data returned from Twitch to a list of Item objects.
    private List<Item> getItemList(String data) throws TwitchException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(data, Item[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to parse item data from Twitch API");
        }
    }

    // Returns the top x streams based on game ID.
    private List<Item> searchStreams(String gameId, int limit) throws TwitchException {
        List<Item> streams = getItemList(searchTwitch(buildSearchURL(STREAM_SEARCH_URL_TEMPLATE, gameId, limit)));
        for (Item item : streams) {
            item.setType(ItemType.STREAM);
            item.setUrl(TWITCH_BASE_URL + item.getBroadcasterName());
        }
        return streams;
    }

    // Returns the top x clips based on game ID.
    private List<Item> searchClips(String gameId, int limit) throws TwitchException {
        List<Item> clips = getItemList(searchTwitch(buildSearchURL(CLIP_SEARCH_URL_TEMPLATE, gameId, limit)));
        for (Item item : clips) {
            item.setType(ItemType.CLIP);
        }
        return clips;
    }

    // Returns the top x videos based on game ID.
    private List<Item> searchVideos(String gameId, int limit) throws TwitchException {
        List<Item> videos = getItemList(searchTwitch(buildSearchURL(VIDEO_SEARCH_URL_TEMPLATE, gameId, limit)));
        for (Item item : videos) {
            item.setType(ItemType.VIDEO);
        }
        return videos;
    }


    // Send HTTP request to Twitch Backend based on the given URL,
    // and return the body of the HTTP response returned from Twitch backend.
    private String searchTwitch(String url) throws TwitchException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // Define the response handler to parse and return HTTP response body returned from Twitch
        ResponseHandler<String> responseHandler = response -> {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 200) {
                System.out.println("Response status: " + response.getStatusLine().getReasonPhrase());
                throw new TwitchException("Failed to get result from Twitch API");
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new TwitchException("Failed to get result from Twitch API");
            }
            JSONObject obj = new JSONObject(EntityUtils.toString(entity));
            return obj.getJSONArray("data").toString();
        };

        try {
            // Define the HTTP request, TOKEN, and CLIENT_ID used for user authentication on Twitch backend
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", TOKEN);
            request.setHeader("Client-Id", CLIENT_ID);
            return httpclient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to get result from Twitch API");
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Game> getGameList(String data) throws TwitchException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(data, Game[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to parse game data from Twitch API");
        }
    }

    public List<Game> topGames(int limit) throws TwitchException {
        if (limit <= 0) {
            limit = DEFAULT_GAME_LIMIT;
        }
        return getGameList(searchTwitch(buildGameURL(TOP_GAME_URL, "", limit)));
    }

    public Game searchGame(String gameName) throws TwitchException {
        List<Game> gameList = getGameList(searchTwitch(buildGameURL(GAME_SEARCH_URL_TEMPLATE, gameName, 0)));
        if (!gameList.isEmpty()) {
            return gameList.getFirst();
        }
        return null;
    }


    // Search items by type (STREAM, VIDEO, CLIP)
    public List<Item> searchByType(String gameId, ItemType type, int limit) throws TwitchException {
        List<Item> items = switch (type) {
            case STREAM -> searchStreams(gameId, limit);
            case VIDEO -> searchVideos(gameId, limit);
            case CLIP -> searchClips(gameId, limit);
        }; // Initialize an empty list

        // Based on the item type (STREAM, VIDEO, CLIP), call the corresponding search method

        // Update the gameId for all items. GameId is used by recommendation function
        for (Item item : items) {
            item.setGameId(gameId);
        }

        return items; // Return the list of items
    }

    // Search items of all types (STREAM, VIDEO, CLIP) for a gameId and return as a map
    public Map<String, List<Item>> searchItems(String gameId) throws TwitchException {
        Map<String, List<Item>> itemMap = new HashMap<>(); // Initialize a new HashMap

        // Iterate through each ItemType (STREAM, VIDEO, CLIP)
        for (ItemType type : ItemType.values()) {
            // Store the list of items by type in the map, using type name as the key
            itemMap.put(type.toString(), searchByType(gameId, type, DEFAULT_SEARCH_LIMIT));
        }

        return itemMap; // Return the map containing items categorized by type
    }

}
