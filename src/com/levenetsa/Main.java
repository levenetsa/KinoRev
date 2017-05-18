package com.levenetsa;

import com.levenetsa.services.ResultService;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(22456);
        ResultService resultService = new ResultService();
        get("/:id", (req, res) -> resultService.getResult(Integer.parseInt(req.params(":id"))));
    }
}
