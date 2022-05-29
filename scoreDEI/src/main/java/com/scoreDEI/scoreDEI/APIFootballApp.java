package com.scoreDEI.scoreDEI;

import java.net.URLEncoder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class APIFootballApp {
    public static void main( String[] args ) throws Exception {
        // Host url
        String host = "https://api-football-v1.p.rapidapi.com/v3/";
        String charset = "UTF-8";
        // Headers for a request
        String x_rapidapi_host = "https://api-football-v1.p.rapidapi.com/v3/";
        String x_rapidapi_key = "b6531a49c7eeb0a1ea6ec42eee699496";//Type here your key
        // Params
        String s = "Pulp";
        // Format query for preventing encoding problems
        String query = String.format("s=%s",
                URLEncoder.encode(s, charset));

        /*
        HttpResponse <String> response = Unirest.get(host + "?" + query)
                .header("x-rapidapi-host", x_rapidapi_host)
                .header("x-rapidapi-key", x_rapidapi_key)
                .asString();
         */
        HttpResponse<String> response = Unirest.get("https://api-football-v1.p.rapidapi.com/v3/players/squads?team=33")
                .header("X-RapidAPI-Host", x_rapidapi_host)
                .header("X-RapidAPI-Key", x_rapidapi_key)
                .asString();
        System.out.println(response.getStatus());
        System.out.println(response.getBody());
        System.out.println(response.getHeaders().get("Content-Type"));

        /*Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(response.getBody());
        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString);*/
    }
}
