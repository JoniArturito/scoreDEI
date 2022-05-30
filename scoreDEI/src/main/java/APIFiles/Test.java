package APIFiles;

import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.scoreDEI.Services.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws Exception {
        //JSON parser object to parse read file
        JsonParser jsonParser = new JsonParser();

        try (FileReader reader = new FileReader("/Users/ruimarques/Documents/GitHub/scoreDEI/scoreDEI/src/main/java/APIFiles/players.json"))
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(reader);
            JsonObject myObject = je.getAsJsonObject();
            JsonArray myArray = myObject.get("response").getAsJsonArray();
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
                for (JsonElement elementStatistic: jsonStatistics){
                    JsonObject jsonStatistic = elementStatistic.getAsJsonObject();
                    JsonObject jsonTeam = jsonStatistic.getAsJsonObject("team");
                    JsonObject jsonGames = jsonStatistic.getAsJsonObject("games");
                    teamName = gson.toJson(jsonTeam.get("name")).replaceAll("\"", "");
                    position = gson.toJson(jsonGames.get("position")).replaceAll("\"", "");
                    break;
                }

                System.out.println(name);
                System.out.println(photoURL);
                System.out.println(teamName);
                System.out.println(position);
                System.out.println(dateString);
                Date date = Date.valueOf(dateString);
                System.out.println(date);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readTeams(FileReader reader) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(reader);
        JsonObject myObject = je.getAsJsonObject();
        JsonArray myArray = myObject.get("response").getAsJsonArray();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (JsonElement resp: myArray) {
            JsonObject respObjectTeam = resp.getAsJsonObject();
            JsonObject jsonTeam = respObjectTeam.getAsJsonObject("team");
            String name = gson.toJson(jsonTeam.get("name")).replaceAll("\"", "");
            String logoURL = gson.toJson(jsonTeam.get("logo")).replaceAll("\"", "");
            URL url = new URL(logoURL);
            BufferedImage bImage = ImageIO.read(url);
            ImageIO.write(bImage, "png", bos );
            byte [] data = bos.toByteArray();
            System.out.println(name);
            System.out.println(logoURL);
            System.out.println(Arrays.toString(data));
        }
            /*
            String prettyJsonString = gson.toJson(myArray);
            System.out.println(prettyJsonString);
             */
    }
}
