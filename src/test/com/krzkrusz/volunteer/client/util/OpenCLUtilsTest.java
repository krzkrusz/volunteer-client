package com.krzkrusz.volunteer.client.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class OpenCLUtilsTest {
    @Test
    public void getDevicesInfo() {
        OpenCLUtils.queryDevices();
    }
}