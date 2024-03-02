namespace com.github.hummel.mpp.lab1;

public interface ITracer
{
    void startTrace();

    void stopTrace();

    TraceResult getTraceResult();
}
