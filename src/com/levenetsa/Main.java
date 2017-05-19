package com.levenetsa;

import com.levenetsa.services.ResultService;
import com.levenetsa.services.SearchService;
import org.codehaus.jackson.map.util.JSONPObject;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(22456);
        ResultService resultService = new ResultService();
        SearchService searchService = new SearchService();
        get("/:id", (req, res) -> resultService.getResult(Integer.parseInt(req.params(":id"))));
        get("/s/:name",  (req, res) -> searchService.getResult(req.params(":name")));
    }
}
