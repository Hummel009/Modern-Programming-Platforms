using Tracer.Serialization.Json;
using Tracer.Serialization.Xml;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Tracer;
using FluentAssertions;

namespace Tests;

[TestClass]
public class SerializationTest
{
    public TraceResult getMultiThreadedTraceResult()
    {
        var methodTrace1 = new MethodTrace(
            "FirstMethod",
            10,
            "FirstClass",
            new List<MethodTrace>(){}
        );
        
        var methodTrace2 = new MethodTrace(
            "SecondMethod",
            20,
            "SecondClass",
            new List<MethodTrace>(){}
        );
        
        var methodTrace3 = new MethodTrace(
            "ThirdMethod",
            30,
            "ThirdClass",
            new List<MethodTrace>(){methodTrace2}
        );

        var methodTrace4 = new MethodTrace(
            "FourthMethod",
            40,
            "FourthClass",
            new List<MethodTrace>(){}
        );

        var methodTrace5 = new MethodTrace(
            "FifthMethod",
            50,
            "FifthClass",
            new List<MethodTrace>(){}
        );

        var methodTrace6 = new MethodTrace(
            "SixthMethod",
            60,
            "SixthClass",
            new List<MethodTrace>(){methodTrace5}
        );

        var threadTrace1 = new ThreadTrace(
            1,
            60,
            new List<MethodTrace>(){methodTrace1, methodTrace3}
        );

        var threadTrace2 = new ThreadTrace(
            2,
            150,
            new List<MethodTrace>(){methodTrace6, methodTrace4}
        );

        return new TraceResult([threadTrace1, threadTrace2]);
    }
    
    [TestMethod]
    public void multiThreadedTraceResultJsonDeserialization()
    {
        var serializer = new TraceResultJsonSerializerImpl();
        
        var content = File.ReadAllText("src/main/resources/multithreaded_trace_result.json");
        var deserializedTraceResult = serializer.deserialize(content);
        
        deserializedTraceResult.Should().Be(getMultiThreadedTraceResult());
    }
    
    [TestMethod]
    public void multiThreadedTraceResultXmlDeserialization()
    {
        var serializer = new TraceResultXmlSerializerImpl();
        
        var content = File.ReadAllText("src/main/resources/multithreaded_trace_result.xml");
        var deserializedTraceResult = serializer.deserialize(content);
        
        deserializedTraceResult.Should().Be(getMultiThreadedTraceResult());
    }
}