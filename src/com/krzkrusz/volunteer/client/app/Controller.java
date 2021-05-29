package com.krzkrusz.volunteer.client.app;

import com.krzkrusz.volunteer.client.service.ClientService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller {

    private ClientService service;
    private AtomicBoolean isGoldbachRunning = new AtomicBoolean(false);
    private AtomicBoolean isFlopsRunning = new AtomicBoolean(false);
    private AtomicBoolean isSortRunning = new AtomicBoolean(false);

    @FXML
    Label label;

    @FXML
    private void initialize() throws IOException {
        service = new ClientService(this);
    }

    public void testGoldbach(ActionEvent actionEvent) {
        if(isGoldbachRunning.get()) {
            service.stopExecution();
            isGoldbachRunning.set(false);
        } else {
            service.startGoldbach();
            isGoldbachRunning.set(true);
        }
    }

    public void testFlops(ActionEvent actionEvent) {
        if(isFlopsRunning.get()) {
            service.stopExecution();
            isFlopsRunning.set(false);
        } else {
            service.startFlops();
            isFlopsRunning.set(true);
        }
    }

    public void testSort(ActionEvent actionEvent) {
        if(isSortRunning.get()) {
            service.stopExecution();
            isSortRunning.set(false);
        } else {
            service.startSort();
            isSortRunning.set(true);
        }
    }

    public void updateTime(String time) {
        label.setText("Execution time = " + time);
    }
}
