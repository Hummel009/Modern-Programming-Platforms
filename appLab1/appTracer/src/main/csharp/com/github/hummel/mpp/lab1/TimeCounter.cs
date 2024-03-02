namespace com.github.hummel.mpp.lab1;

public class TimeCounter
{
    public long count(IEnumerable<MethodTrace> methodTraces)
    {
        long res = 0;

        foreach (var methodTrace in methodTraces)
        {
            res += methodTrace.getMilliseconds();
            res += count(methodTrace.nestedMethodTraces);
        }

        return res;
    }
}