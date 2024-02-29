using Microsoft.VisualStudio.TestTools.UnitTesting;
using Tracer;
using FluentAssertions;

namespace Tests;

[TestClass]
public class TimeCounterTest
{
    private readonly TimeCounter timeCounter = new TimeCounter();
    
    [TestMethod]
    public void сountEmptyEnumerableReturnZero()
    {
        var enumerable = new List<MethodTrace>();
        timeCounter.count(enumerable).Should().Be(0);
    }
    
    [TestMethod]
    public void сountRecursiveEnumerable()
    {
        var methodTrace1 = new MethodTrace(
            "AMOGUS",
            10,
            "Some",
            new List<MethodTrace>(){}
        );

        var methodTrace2 = new MethodTrace(
            "AMOGUS",
            20,
            "Some",
            new List<MethodTrace>(){}
        );

        var methodTrace3 = new MethodTrace(
            "AMOGUS",
            30,
            "Some",
            new List<MethodTrace>(){}
        );

        var methodTrace4 = new MethodTrace(
            "name",
            40,
            "className",
            new List<MethodTrace>(){methodTrace1, methodTrace2}
            );

        var methodTrace5 = new MethodTrace(
            "AMOGUS",
            50,
            "Some",
            new List<MethodTrace>(){}
        );
        
        var methodTrace6 = new MethodTrace(
            "name",
            60,
            "className",
            new List<MethodTrace>(){methodTrace5}
            );        
        
        var methodTrace7 = new MethodTrace(
            "name",
            70,
            "className",
            new List<MethodTrace>(){methodTrace6}
            );

        var methodTraces = new List<MethodTrace>()
        {
            methodTrace3,
            methodTrace4,
            methodTrace7
        };

        timeCounter.count(methodTraces).Should().Be(280);
    }
}