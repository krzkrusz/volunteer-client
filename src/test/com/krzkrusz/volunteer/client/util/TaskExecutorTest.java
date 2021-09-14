package com.krzkrusz.volunteer.client.util;

import org.junit.Test;

import javax.script.ScriptException;
import java.io.IOException;

import static org.junit.Assert.*;

public class TaskExecutorTest {

    @Test
    public void executeKernelJSTest() throws NoSuchMethodException, ScriptException, IOException {
        String result = (String) TaskExecutor.execute(TaskExecutor.KERNEL_PATH, null, null);
        System.out.println(result);
    }

    @Test
    public void executeKernelJSgoldbach() throws NoSuchMethodException, ScriptException, IOException {
        String result = (String) TaskExecutor.execute(TaskExecutor.KERNEL_GOLDBACH_PATH, null, null);
        System.out.println(result);
    }

    @Test
    public void executeKernel() {
        TaskExecutor.executeKernel();
    }



}