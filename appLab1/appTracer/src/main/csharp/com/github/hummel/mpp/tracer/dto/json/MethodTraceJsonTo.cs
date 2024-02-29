using System.Runtime.Serialization;

namespace Tracer.Serialization.Json;
#pragma warning disable CS8618

[DataContract]
public class MethodTraceJsonTo
{
    private long time;
    
    [DataMember(Name = "name", Order = 1)]
    public string methodName;

    [DataMember(Name = "class", Order = 2)]
    public string className;

    [DataMember(Name = "time", Order = 3)]
    public string timeValue
    {
        get => time + "ms";
        set => time = long.Parse(value[..^2]);
    }
    
    [DataMember(Name = "methods", Order = 4)]
    public List<MethodTraceJsonTo> nestedTraces;
    
    public MethodTraceJsonTo(MethodTrace methodTrace)
    {
        time = methodTrace.getMilliseconds();
        className = methodTrace.getClassName();
        methodName = methodTrace.getName();

        nestedTraces = new List<MethodTraceJsonTo>();
        foreach (MethodTrace trace in methodTrace.getMethodTraces())
        {
            nestedTraces.Add(new MethodTraceJsonTo(trace));
        }
    }

    public MethodTraceJsonTo()
    {
    }

    public MethodTrace methodTrace =>
        new(
            methodName,
            time,
            className,
            (from nestedTrace in nestedTraces
                select nestedTrace.methodTrace).ToList()
        );
}