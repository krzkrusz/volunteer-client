print("GPU in JS!")

//some typedefs
var String = java.lang.String;
var System = java.lang.System;
var CL = org.jocl.CL;
var Pointer = org.jocl.Pointer;
var Sizeof = org.jocl.Sizeof;
var cl_command_queue = org.jocl.cl_command_queue;
var cl_context = org.jocl.cl_context;
var cl_context_properties = org.jocl.cl_context_properties;
var cl_device_id = org.jocl.cl_device_id;
var cl_kernel = org.jocl.cl_kernel;
var cl_mem = org.jocl.cl_mem;
var cl_platform_id =  org.jocl.cl_platform_id;
var cl_program = org.jocl.cl_program;
var LongArray = Java.type("long[]");
var ByteArray = Java.type("byte[]");
var FloatArray = Java.type("float[]");
var StringArray = Java.type("java.lang.String[]");
var IntArray = Java.type("int[]");
var ClPlatformIdArray = Java.type("org.jocl.cl_platform_id[]");
var ClDeviceIdArray = Java.type("org.jocl.cl_device_id[]");
var ClMemArray = Java.type("org.jocl.cl_mem[]");

//helper function
function getString(device, paramName) {
    var size = new LongArray(1);
    CL.clGetDeviceInfo(device, paramName, 0, null, size);
    var buffer = new ByteArray(size[0]);
    CL.clGetDeviceInfo(device, paramName, buffer.length, Pointer.to(buffer), null);
    return new String(buffer, 0, buffer.length-1);
}

//this goes to GPU
var programSource =
    "__kernel void "+
    "sampleKernel(__global const float *a,"+
    "             __global const float *b,"+
    "             __global float *c)"+
    "{"+
    "    int gid = get_global_id(0);"+
    "    c[gid] = a[gid] + b[gid];"+
    "}";

var start= System.currentTimeMillis();

var n = 1024;
var srcArrayA = new FloatArray(n);
var srcArrayB = new FloatArray(n);
var dstArray = new FloatArray(n);
for (var i=0; i<n; i++){
    srcArrayA[i] = i;
    srcArrayB[i] = i;
}
var srcA = Pointer.to(srcArrayA);
var srcB = Pointer.to(srcArrayB);
var dst = Pointer.to(dstArray);


var platformIndex = 0;
var deviceType = CL.CL_DEVICE_TYPE_ALL;
var deviceIndex = 2;

CL.setExceptionsEnabled(true);

var numPlatformsArray = new IntArray(1);
CL.clGetPlatformIDs(0, null, numPlatformsArray);
var numPlatforms = numPlatformsArray[0];

var platforms = new ClPlatformIdArray(numPlatforms);
CL.clGetPlatformIDs(platforms.length, platforms, null);
var platform = platforms[platformIndex];

var contextProperties = new cl_context_properties();
contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

var numDevicesArray = new IntArray(1);
CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
var numDevices = numDevicesArray[0];

//just print out the name
var devices = new ClDeviceIdArray(numDevices);
CL.clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
var device = devices[deviceIndex];

var deviceName = getString(device, CL.CL_DEVICE_NAME);
print("Device: "+deviceName);

var devices = new ClDeviceIdArray(1);
devices[0]=device;
var context = CL.clCreateContext(
    contextProperties, 1, devices,
    null, null, null);

var commandQueue =
    CL.clCreateCommandQueue(context, device, 0, null);

var memObjects = new ClMemArray(3);
memObjects[0] = CL.clCreateBuffer(context,
    CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR,
    Sizeof.cl_float * n, srcA, null);
memObjects[1] = CL.clCreateBuffer(context,
    CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR,
    Sizeof.cl_float * n, srcB, null);
memObjects[2] = CL.clCreateBuffer(context,
    CL.CL_MEM_READ_WRITE,
    Sizeof.cl_float * n, null, null);

var ps = new StringArray(1);
ps[0] = programSource;
var program = CL.clCreateProgramWithSource(context,
    1, ps, null, null);

CL.clBuildProgram(program, 0, null, null, null, null);

var kernel = CL.clCreateKernel(program, "sampleKernel", null);

CL.clSetKernelArg(kernel, 0,
    Sizeof.cl_mem, Pointer.to(memObjects[0]));
CL.clSetKernelArg(kernel, 1,
    Sizeof.cl_mem, Pointer.to(memObjects[1]));
CL.clSetKernelArg(kernel, 2,
    Sizeof.cl_mem, Pointer.to(memObjects[2]));

var global_work_size = new LongArray(1);
global_work_size[0] = n;
var local_work_size = new LongArray(1);
local_work_size[0] = 1;

// Execute the kernel
CL.clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
    global_work_size, local_work_size, 0, null, null);

CL.clEnqueueReadBuffer(commandQueue, memObjects[2], CL.CL_TRUE, 0,
    n * Sizeof.cl_float, dst, 0, null, null);

CL.clReleaseMemObject(memObjects[0]);
CL.clReleaseMemObject(memObjects[1]);
CL.clReleaseMemObject(memObjects[2]);
CL.clReleaseKernel(kernel);
CL.clReleaseProgram(program);
CL.clReleaseCommandQueue(commandQueue);
CL.clReleaseContext(context);

print("Calculated on GPU:");

for (var i=0; i<n; i++){
    print(srcArrayA[i], " + ",srcArrayB[i]," = ",dstArray[i]);
}

var result = "Done in: "+(System.currentTimeMillis()-start)+" ms.";