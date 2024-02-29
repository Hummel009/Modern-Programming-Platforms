using System.Runtime.Serialization;

namespace Tracer.Serialization.Json;
#pragma warning disable CS8618

[DataContract]
public class TraceResultJsonTo
{
    [DataMember(Name = "threads")]
    public List<ThreadTraceJsonTo> threadTraces;
    
    public TraceResultJsonTo(TraceResult traceResult)
    {
        threadTraces = new List<ThreadTraceJsonTo>();
        foreach (ThreadTrace threadTrace in traceResult.nestedThreadTraces)
        {
            threadTraces.Add(new ThreadTraceJsonTo(threadTrace));
        }
    }

    public TraceResultJsonTo()
    {
    }

    public TraceResult traceResult =>
        new(
            (from threadTrace in threadTraces 
                select threadTrace.threadTrace).ToList()
        );
}