namespace com.github.hummel.mpp.lab1;

public interface ITraceResultSerializer
{
    string serialize(TraceResult traceResult);

    TraceResult? deserialize(string content);
}
