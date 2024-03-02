namespace com.github.hummel.mpp.lab1;

using Microsoft.VisualStudio.TestTools.UnitTesting;
using FluentAssertions;

[TestClass]
public class TracerTest
{
    private TraceResult getExpectedTraceResult(int thread1Id, int thread2Id)
    {
        var methodTrace1 = new MethodTrace(
            "InnerMethod",
            0L,
            "Bar",
            new List<MethodTrace>() { }
        );

        var methodTrace2 = new MethodTrace(
            "MyMethod",
            0L,
            "Foo",
            new List<MethodTrace>() { methodTrace1 }
        );

        var methodTrace3 = new MethodTrace(
            "InnerMethod",
            0L,
            "Bar",
            new List<MethodTrace>() { }
        );

        var threadTrace1 = new ThreadTrace(
            thread1Id,
            0L,
            new List<MethodTrace>() { methodTrace2 }
        );

        var threadTrace2 = new ThreadTrace(
            thread2Id,
            0L,
            new List<MethodTrace>() { methodTrace3 }
        );

        var res = new TraceResult(
            new List<ThreadTrace>() { threadTrace1, threadTrace2 }
        );

        return res;
    }

    [TestMethod]
    public void multiThreadedTraceResult()
    {
        var tracer = new TracerImpl();

        var foo = new Foo(tracer);
        var bar = new Bar(tracer);

        var thread1 = new Thread(foo.MyMethod);
        var thread2 = new Thread(bar.InnerMethod);

        thread1.Start();
        thread1.Join();

        thread2.Start();
        thread2.Join();

        var actualTraceResult = tracer.getTraceResult();

        var thread1Id = thread1.ManagedThreadId;
        var thread2Id = thread2.ManagedThreadId;

        var expectedTraceResult = getExpectedTraceResult(thread1Id, thread2Id);

        actualTraceResult.Should().Be(expectedTraceResult);
    }
}