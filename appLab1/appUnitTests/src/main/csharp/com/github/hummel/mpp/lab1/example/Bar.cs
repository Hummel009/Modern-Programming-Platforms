namespace com.github.hummel.mpp.lab1;

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