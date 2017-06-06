package com.levenetsa.fetcher;

import com.levenetsa.fetcher.services.ResultService;
import com.levenetsa.fetcher.services.SearchService;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        port(22456);
        ResultService resultService = new ResultService();
        SearchService searchService = new SearchService();
        get("addfilm/:id", (req, res) -> resultService.addFilm(Integer.parseInt(req.params(":id"))));
        get("addfilms/:id/:last", (req, res) -> resultService.addFilms(Integer.parseInt(req.params(":id")),
                Integer.parseInt(req.params(":last"))));
        get("loadreviews", (req, res) -> resultService.loadReviews());
        get("/:id", (req, res) -> resultService.getResult(Integer.parseInt(req.params(":id"))));
        get("/recount/:id", (req, res) -> resultService.recountResult(Integer.parseInt(req.params(":id"))));
        get("/s/:name", (req, res) -> searchService.getResult(req.params(":name")));
        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
                });
        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    }
}
