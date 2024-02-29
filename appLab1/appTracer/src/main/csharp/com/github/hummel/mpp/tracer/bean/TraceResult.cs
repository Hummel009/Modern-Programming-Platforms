namespace Tracer;

public class TraceResult
{
    private readonly List<ThreadTrace> threadTraces;

    public IReadOnlyList<ThreadTrace> nestedThreadTraces => threadTraces;
    
    public TraceResult(List<ThreadTrace> threadTraces)
    {
        this.threadTraces = threadTraces;
    }
    
    public TraceResult()
    {
        threadTraces = new List<ThreadTrace>();
    }

    public override bool Equals(object? obj)
    {
        if (obj == null || obj.GetType() != typeof(TraceResult))
        {
            return false;
        }

        TraceResult traceResult = (TraceResult)obj;
        return threadTraces.SequenceEqual(traceResult.threadTraces);
    }

    public override int GetHashCode()
    {
        int hash = 13;
        
        foreach (ThreadTrace threadTraceInfo in threadTraces)
        {
            hash = unchecked((7 * hash) + threadTraceInfo.GetHashCode());
        }
        
        return hash;
    }
}