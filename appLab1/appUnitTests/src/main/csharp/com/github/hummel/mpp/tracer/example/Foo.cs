using Tracer;

namespace Tests.ExampleClasses;

public class Foo
{
    private Bar bar;
    private ITracer tracer;

    internal Foo(ITracer tracer)
    {
        this.tracer = tracer;
        bar = new Bar(tracer);
    }

    public void MyMethod()
    {
        tracer.startTrace();
        
        Console.WriteLine("Before _bar.InnerMethod()");
        
        bar.InnerMethod();
        
        Console.WriteLine("After _bar.InnerMethod()");
        
        tracer.stopTrace();
    }
}