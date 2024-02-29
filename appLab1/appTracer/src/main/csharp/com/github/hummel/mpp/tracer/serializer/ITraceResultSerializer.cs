namespace Tracer.Serialization;

public interface ITraceResultSerializer
{
    string serialize(TraceResult traceResult);
    
    TraceResult? deserialize(string content);
}