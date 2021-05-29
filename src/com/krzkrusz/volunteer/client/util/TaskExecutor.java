package com.krzkrusz.volunteer.client.util;

import com.krzkrusz.volunteer.client.model.Result;
import javafx.application.Platform;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TaskExecutor {

    public static String GOLDBACH_PATH = "src/main/resources/goldbach_conjecture.js";
    public static String FLOPS_PATH = "src/main/resources/flops.js";
    public static String BUBBLE_SORT_PATH = "src/main/resources/bubbleSort.js";

    public static Object execute(String path, String functionName, Object... args) throws ScriptException, NoSuchMethodException, IOException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        // read script file
        engine.eval(Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8));

        Invocable inv = (Invocable) engine;
        long startTime = System.currentTimeMillis();
        // call function from script file
        Object result = inv.invokeFunction(functionName, args);
        long stopTime = System.currentTimeMillis();

        System.out.println("ExecutionTime = " + (stopTime - startTime));
        System.out.println(result);
        return result;
    }

    public static Result executeFromString(String code, String functionName, Object... args) throws ScriptException, NoSuchMethodException, IOException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        // read script file
        engine.eval(code);

        Invocable inv = (Invocable) engine;
        long startTime = System.currentTimeMillis();
        // call function from script file
        Object result = inv.invokeFunction(functionName, args);
        long stopTime = System.currentTimeMillis();
        System.out.println("ExecutionTime = " + (stopTime - startTime));
        System.out.println(result);
        return new Result(result,stopTime - startTime);
    }
}
