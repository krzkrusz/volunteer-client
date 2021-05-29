package com.krzkrusz.volunteer.client.service;

import com.krzkrusz.volunteer.client.app.Controller;
import com.krzkrusz.volunteer.client.model.ComputationResult;
import com.krzkrusz.volunteer.client.model.FunctionDTO;
import com.krzkrusz.volunteer.client.model.Result;
import com.krzkrusz.volunteer.client.util.JerseyClient;
import com.krzkrusz.volunteer.client.util.TaskExecutor;
import javafx.application.Platform;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientService {

    private final Controller controller;
    ExecutorService executor = null;
    JerseyClient jerseyClient = new JerseyClient();

    public ClientService(Controller controller) throws IOException {
        this.controller = controller;
    }


    public void startGoldbach() {
        if (executor == null || executor.isShutdown())
            executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    FunctionDTO dto = jerseyClient.getGoldbachFunction();
                    Result result = TaskExecutor.executeFromString(dto.getFunction(), dto.getFunctionName(), dto.getArgs());
                    setComputationTime(String.valueOf(result.getTimeInMillis()));

                    jerseyClient.sendComputationResult(new ComputationResult(dto.getId(), result.getResult().toString()));
                    //TaskExecutor.execute(TaskExecutor.GOLDBACH_PATH, "goldbach", "100000");
                }
            } catch (ScriptException | NoSuchMethodException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void startFlops() {
        if (executor == null || executor.isShutdown())
            executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    FunctionDTO dto = jerseyClient.getFlopsFunction();
                    Result result = TaskExecutor.executeFromString(dto.getFunction(), dto.getFunctionName(), dto.getArgs());
                    setComputationTime(String.valueOf(result.getTimeInMillis()));
                    jerseyClient.sendComputationResult(new ComputationResult(dto.getId(), result.getResult().toString()));
                    //TaskExecutor.execute(TaskExecutor.GOLDBACH_PATH, "goldbach", "100000");
                }
            } catch (ScriptException | NoSuchMethodException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void startSort() {
        if (executor == null || executor.isShutdown())
            executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    FunctionDTO dto = jerseyClient.getSortFunction();
                    Result result = TaskExecutor.executeFromString(dto.getFunction(), dto.getFunctionName(), dto.getArgs());
                    setComputationTime(String.valueOf(result.getTimeInMillis()));
                    jerseyClient.sendComputationResult(new ComputationResult(dto.getId(), result.getResult().toString()));
                    //TaskExecutor.execute(TaskExecutor.GOLDBACH_PATH, "goldbach", "100000");
                }
            } catch (ScriptException | NoSuchMethodException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setComputationTime(String time) {
        Platform.runLater(() -> {
            controller.updateTime(time);
        });
    }

    public void stopExecution() {
        executor.shutdownNow();
    }
}
