using System.Reflection;
using System.Diagnostics;

namespace Tracer;

public class MetricsEngine
{
    private readonly Stopwatch stopwatch = new Stopwatch();

    private readonly string methodName;

    private readonly string methodClassName;

    private readonly List<MethodTrace> nestedTraces = new List<MethodTrace>();

    private MethodTrace? methodTrace = null;

    internal MethodTrace MethodTrace =>
        methodTrace ??= new MethodTrace(
            methodName,
            stopwatch.ElapsedMilliseconds,
            methodClassName,
            nestedTraces);

    public MetricsEngine(MethodBase methodBase)
    {
        methodName = methodBase.Name;
        Type? methodClassType = methodBase.DeclaringType;
        if (methodClassType == null)
        {
            throw new Exception("Method class type is null");
        }
        
        methodClassName = methodClassType.Name;
    }

    public void startTimer()
    {
        stopwatch.Start();
    }

    public void stopTimer()
    {
        stopwatch.Stop();
    }

    internal void addMethodTrace(MethodTrace methodTrace)
    {
        nestedTraces.Add(methodTrace);
    }
}