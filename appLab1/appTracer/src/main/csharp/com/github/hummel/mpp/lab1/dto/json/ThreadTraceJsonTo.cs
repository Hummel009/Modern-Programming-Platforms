namespace com.github.hummel.mpp.lab1;

using System.Runtime.Serialization;

#pragma warning disable CS8618

[DataContract]
public class ThreadTraceJsonTo
{
    private long time;

    public int id;

    [DataMember(Name = "id", Order = 1)]
    public string idStringValue
    {
        get => id.ToString();
        set => id = int.Parse(value);
    }

    [DataMember(Name = "time", Order = 2)]
    public string timeValue
    {
        get => time + "ms";
        set => time = long.Parse(value[..^2]);
    }

    [DataMember(Name = "methods", Order = 3)]
    public List<MethodTraceJsonTo> methodsTraces;

    public ThreadTraceJsonTo(ThreadTrace threadTrace)
    {
        fromThreadTrace(threadTrace);
    }

    public ThreadTraceJsonTo()
    {
    }

    public void fromThreadTrace(ThreadTrace threadTrace)
    {
        time = threadTrace.getTime();
        id = threadTrace.getThreadId();
        time = threadTrace.getTime();

        methodsTraces = new List<MethodTraceJsonTo>();
        foreach (MethodTrace methodTrace in threadTrace.nestedMethodTraces)
        {
            methodsTraces.Add(new MethodTraceJsonTo(methodTrace));
        }
    }

    public ThreadTrace threadTrace =>
        new(
            id,
            time,
            (from methodTrace in methodsTraces
             select methodTrace.methodTrace).ToList()
        );
}