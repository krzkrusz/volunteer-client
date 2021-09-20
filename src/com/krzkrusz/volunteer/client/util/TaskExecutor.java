package com.krzkrusz.volunteer.client.util;

import com.krzkrusz.volunteer.client.model.Result;
import javafx.application.Platform;
import org.jocl.*;

import static org.jocl.CL.*;

import javax.script.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskExecutor {

    public static String GOLDBACH_PATH = "src/main/resources/goldbach_conjecture.js";
    public static String FLOPS_PATH = "src/main/resources/flops.js";
    public static String BUBBLE_SORT_PATH = "src/main/resources/bubbleSort.js";
    public static String KERNEL_PATH = "src/main/resources/kernelJStest.js";
    public static String KERNEL_GOLDBACH_PATH = "src/main/resources/kernelJSgoldbach.js";


    public static Object execute(String path, String functionName, Object... args) throws ScriptException, NoSuchMethodException, IOException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        // read script file
        long startTime = System.currentTimeMillis();

        String result = (String) engine.eval(Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8));

//        Invocable inv = (Invocable) engine;
//        // call function from script file
//        Object result = inv.invokeFunction(functionName, args);
        long stopTime = System.currentTimeMillis();

        System.out.println("ExecutionTime = " + (stopTime - startTime));
        System.out.println(result);
        return new Result(result, stopTime-startTime);
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
        return new Result(result, stopTime - startTime);
    }



    public static Result executeKernel() {
        long startTime = System.currentTimeMillis();

        String programSource =
                "int is_prime(int n){"+
                        "for (int j = 2; j <= sqrt(n); j++)"+
                        "{"+
                        "if((n%j) == 0){"+
                        "return 0;"+
                        "}"+
                        "}"+
                        "return 1;"+
                        "}"+

                        "__kernel void sampleKernel(__global const int *a,"+
                        "__global int *c)"+
                        "{"+

                        "int gid = get_global_id(0);"+
                        "int result = 0;"+
                        "int i = 2;"+

                        "if(a[gid]>2 && a[gid]%2==0){"+
                        "for (int j = a[gid]-i; j > 2; j--)"+
                        "{"+
                        "if(is_prime(i) == 1 && is_prime(j) == 1)"+
                        "{"+
                        "result = 1;"+
                        "break;"+
                        "}"+
                        "i++;"+
                        "}"+
                        "}"+

                        "c[gid] = result;"+
                        "}";

        int n = 2;
        int srcArrayA[] = new int[n];
        int dstArray[] = new int[n];
            srcArrayA[0] = 200000019;
            srcArrayA[1] = 200000016;
        Pointer srcA = Pointer.to(srcArrayA);
        Pointer dst = Pointer.to(dstArray);


        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL.CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        CL.clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];
        System.out.println("Number of platforms: " + numPlatformsArray[0]);

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        CL.clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id devices[] = new cl_device_id[numDevices];
        CL.clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        cl_context context = CL.clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create a command-queue for the selected device
        cl_command_queue commandQueue =
                CL.clCreateCommandQueue(context, device, 0, null);

        // Allocate the memory objects for the input and output data
        cl_mem memObjects[] = new cl_mem[2];
        memObjects[0] = CL.clCreateBuffer(context,
                CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_int * n, srcA, null);
        memObjects[1] = CL.clCreateBuffer(context,
                CL.CL_MEM_READ_WRITE,
                Sizeof.cl_int * n, null, null);

        // Create the program from the source code
        cl_program program = CL.clCreateProgramWithSource(context,
                1, new String[]{programSource}, null, null);

        // Build the program
        CL.clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        cl_kernel kernel = CL.clCreateKernel(program, "sampleKernel", null);

        // Set the arguments for the kernel
        CL.clSetKernelArg(kernel, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        CL.clSetKernelArg(kernel, 1,
                Sizeof.cl_mem, Pointer.to(memObjects[1]));

        // Set the work-item dimensions
        long global_work_size[] = new long[]{n};
        long local_work_size[] = new long[]{1};

        // Execute the kernel
        CL.clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                global_work_size, local_work_size, 0, null, null);

        // Read the output data
        CL.clEnqueueReadBuffer(commandQueue, memObjects[1], CL.CL_TRUE, 0,
                n * Sizeof.cl_int, dst, 0, null, null);

        // Release kernel, program, and memory objects
        CL.clReleaseMemObject(memObjects[0]);
        CL.clReleaseMemObject(memObjects[1]);
        CL.clReleaseKernel(kernel);
        CL.clReleaseProgram(program);
        CL.clReleaseCommandQueue(commandQueue);
        CL.clReleaseContext(context);

        for(int i = 0; i<n; i++) {
            System.out.println(srcArrayA[i] + "___" + dstArray[i]);
        }
        long stopTime = System.currentTimeMillis();
        System.out.println("ExecutionTime = " + (stopTime - startTime));
        return new Result(null, stopTime - startTime);
    }
}
