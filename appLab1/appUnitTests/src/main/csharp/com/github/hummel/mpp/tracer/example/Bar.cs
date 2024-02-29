namespace Tests.ExampleClasses;
using Tracer;

public class Bar
{
    private ITracer tracer;

    internal Bar(ITracer tracer)
    {
        this.tracer = tracer;
    }

    public void InnerMethod()
    {
        tracer.startTrace();
        Console.WriteLine("In Bar.InnerMethod()");
        tracer.stopTrace();
    }
}