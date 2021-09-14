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

var start= System.currentTimeMillis();

var n = 2;
var srcArrayA = new IntArray(n);
var dstArray = new IntArray(n);
srcArrayA[0] = 19;
srcArrayA[1] = 16
var srcA = Pointer.to(srcArrayA);
var dst = Pointer.to(dstArray);


var platformIndex = 0;
var deviceType = CL.CL_DEVICE_TYPE_ALL;
var deviceIndex = 0;

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

var memObjects = new ClMemArray(2);
memObjects[0] = CL.clCreateBuffer(context,
    CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR,
    Sizeof.cl_int * n, srcA, null);
memObjects[1] = CL.clCreateBuffer(context,
    CL.CL_MEM_READ_WRITE,
    Sizeof.cl_int * n, null, null);

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

var global_work_size = new LongArray(1);
global_work_size[0] = n;
var local_work_size = new LongArray(1);
local_work_size[0] = 1;

// Execute the kernel
CL.clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
    global_work_size, local_work_size, 0, null, null);

CL.clEnqueueReadBuffer(commandQueue, memObjects[1], CL.CL_TRUE, 0,
    n * Sizeof.cl_int, dst, 0, null, null);

CL.clReleaseMemObject(memObjects[0]);
CL.clReleaseMemObject(memObjects[1]);
CL.clReleaseKernel(kernel);
CL.clReleaseProgram(program);
CL.clReleaseCommandQueue(commandQueue);
CL.clReleaseContext(context);

print("Calculated on GPU:");

for (var i=0; i<n; i++){
    print(srcArrayA[i], "____", dstArray[i]);
}

var result = "Done in: "+(System.currentTimeMillis()-start)+" ms.";
result;