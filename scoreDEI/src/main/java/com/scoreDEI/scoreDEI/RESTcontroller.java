/**
 * It's a REST controller that handles requests to the API
 */
package com.scoreDEI.scoreDEI;

import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.sql.Date;
import java.util.Optional;

@RestController
@RequestMapping("rest")
public class RESTcontroller {
    @Autowired
    UserService userService;

    @Autowired
    TeamService teamService;

    @Autowired
    PlayerService playerService;

    @Autowired
    GameService gameService;

    @Autowired
    EventService eventService;

    // Host url
    String host = "https://v3.football.api-sports.io/";
    String charset = "UTF-8";
    // Headers for a request
    String x_rapidapi_host = "v3.football.api-sports.io";
    String x_rapidapi_key = "b6531a49c7eeb0a1ea6ec42eee699496";//Type here your key

    /**
     * This function is used to check the status of the API
     *
     * @param model This is the model object that will be used to pass data to the view.
     * @return A JSON object containing the status of the API.
     */
    @GetMapping(value = "/checkstatus", produces = {MediaType.APPLICATION_JSON_VALUE})
    public String checkAPIStatus(Model model) throws Exception {
        HttpResponse<JsonNode> response = Unirest.get(host.concat("status"))
                .header("X-RapidAPI-Host", x_rapidapi_host)
                .header("X-RapidAPI-Key", x_rapidapi_key)
                .asJson();
        /*System.out.println(response.getStatus());
        System.out.println(response.getBody());
        System.out.println(response.getHeaders().get("Content-Type"));*/

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(String.valueOf(response.getBody()));
        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString);

        return prettyJsonString;
    }

    /**
     * It takes in a league and season, makes a request to the API, parses the response, and adds the teams to the database
     *
     * @param league The league ID.
     * @param season The year of the season (e.g. 2019)
     * @param model This is the model that will be passed to the view.
     * @return A JSON string.
     */
    @GetMapping(value = "getTeams", produces = {MediaType.APPLICATION_JSON_VALUE})
    public String getTeamsFromAPI(@RequestParam(name = "league", required=true) int league, @RequestParam(name = "season", required=true) int season, Model model) throws Exception{
        String query = String.format("?league=%d&season=%d",
                league, season);
        HttpResponse<JsonNode> response = Unirest.get(host.concat("teams").concat(query))
                .header("X-RapidAPI-Host", x_rapidapi_host)
                .header("X-RapidAPI-Key", x_rapidapi_key)
                .asJson();
        /*System.out.println(response.getStatus());
        System.out.println(response.getBody());
        System.out.println(response.getHeaders().get("Content-Type"));*/

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(String.valueOf(response.getBody()));
        JsonObject myObject = je.getAsJsonObject();
        JsonArray myArray = myObject.get("response").getAsJsonArray();
        for (JsonElement resp: myArray) {
            JsonObject respObjectTeam = resp.getAsJsonObject();
            JsonObject jsonTeam = respObjectTeam.getAsJsonObject("team");
            String name = gson.toJson(jsonTeam.get("name")).replaceAll("\"", "");
            String logoURL = gson.toJson(jsonTeam.get("logo")).replaceAll("\"", "");
            URL url = new URL(logoURL);
            BufferedImage bImage = ImageIO.read(url);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "png", bos );
            byte [] data = bos.toByteArray();
            /*
            System.out.println(name);
            System.out.println(logoURL);
            System.out.println(Arrays.toString(data));
             */
            teamService.addTeam(new Team(name, data));
        }

        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString);

        return prettyJsonString;
    }

    /**
     * It gets the players from the API and saves them in the database
     *
     * @param league The league ID.
     * @param season The season of the league.
     * @param page The page number of the results.
     * @param model The model object is used to pass data from the controller to the view.
     * @return JSON
     */
    @GetMapping(value = "getPlayers", produces = {MediaType.APPLICATION_JSON_VALUE})
    public String getPlayersFromAPI(@RequestParam(name = "league", required=true) int league, @RequestParam(name = "season", required=true) int season, @RequestParam(name = "page", required = true, defaultValue = "1") int page, Model model) throws Exception{
        String query = String.format("?league=%d&season=%d&page=%d",
                league, season, page);
        HttpResponse<JsonNode> response = Unirest.get(host.concat("players").concat(query))
                .header("X-RapidAPI-Host", x_rapidapi_host)
                .header("X-RapidAPI-Key", x_rapidapi_key)
                .asJson();
        /*System.out.println(response.getStatus());
        System.out.println(response.getBody());
        System.out.println(response.getHeaders().get("Content-Type"));*/

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(String.valueOf(response.getBody()));
        JsonObject myObject = je.getAsJsonObject();
        JsonArray myArray = myObject.get("response").getAsJsonArray();
        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString);
        for (JsonElement resp: myArray) {
            JsonObject respObjectPlayer = resp.getAsJsonObject();
            JsonObject jsonPlayer = respObjectPlayer.getAsJsonObject("player");
            JsonArray jsonStatistics = respObjectPlayer.getAsJsonArray("statistics");
            JsonObject jsonDate = jsonPlayer.getAsJsonObject("birth");

            String name = gson.toJson(jsonPlayer.get("name")).replaceAll("\"", "");
            String photoURL = gson.toJson(jsonPlayer.get("photo")).replaceAll("\"", "");
            String dateString = gson.toJson(jsonDate.get("date")).replaceAll("\"", "");
            String teamName = "";
            String position = "";
            for (JsonElement elementStatistic : jsonStatistics) {
                JsonObject jsonStatistic = elementStatistic.getAsJsonObject();
                JsonObject jsonTeam = jsonStatistic.getAsJsonObject("team");
                JsonObject jsonGames = jsonStatistic.getAsJsonObject("games");
                teamName = gson.toJson(jsonTeam.get("name")).replaceAll("\"", "");
                position = gson.toJson(jsonGames.get("position")).replaceAll("\"", "");
                break;
            }
            /*
            System.out.println(name);
            System.out.println(photoURL);
            System.out.println(teamName);
            System.out.println(position);
            System.out.println(dateString);
             */
            Date date;
            if (dateString == null || dateString.equals("null")){
                date = Date.valueOf("1900-01-01");
            }
            else{
                date = Date.valueOf(dateString);
            }
            Optional<Team> opTeam = teamService.getTeam(teamName);
            if (opTeam.isPresent()) {
                Team team = opTeam.get();
                Player dbPlayer = new Player(name, position, date, team);
                dbPlayer.setUrlPhoto(photoURL);
                if (!this.playerService.isPlayerExist(dbPlayer)) {
                    playerService.addPlayer(dbPlayer);
                }
            }
        }

        return prettyJsonString;
    }
}
