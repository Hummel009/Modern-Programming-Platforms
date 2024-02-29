using YAXLib.Attributes;
using YAXLib.Enums;

namespace Tracer.Serialization.Xml;

public class ThreadTraceXmlTo
{
    private long time;

    public int id;

    [YAXAttributeForClass]
    [YAXSerializeAs("id")]
    public string idStringValue
    {
        get => id.ToString();
        set => id = int.Parse(value);
    }
    
    [YAXAttributeForClass]
    [YAXSerializeAs("time")]
    public string timeValue
    {
        get => time + "ms";
        set => time = long.Parse(value[..^2]);
    }

    [YAXCollection(YAXCollectionSerializationTypes.RecursiveWithNoContainingElement, EachElementName = "method")]
    public List<MethodTraceXmlTo> methodsTraces
    { get; set; }

    #pragma warning disable CS8618
    public ThreadTraceXmlTo(ThreadTrace threadTrace)

    {
        fromThreadTrace(threadTrace);
    }

    public ThreadTraceXmlTo()
    {
        methodsTraces = new List<MethodTraceXmlTo>();
    }

    public void fromThreadTrace(ThreadTrace threadTrace)
    {
        time = threadTrace.getTime();
        id = threadTrace.getThreadId();
        time = threadTrace.getTime();
        
        methodsTraces = new List<MethodTraceXmlTo>();
        foreach (MethodTrace methodTrace in threadTrace.nestedMethodTraces)
        {
            methodsTraces.Add(new MethodTraceXmlTo(methodTrace));
        }
    }
    
    internal ThreadTrace threadTrace =>
        new(
            id,
            time,
            (from methodTrace in methodsTraces
                select methodTrace.methodTrace).ToList()
        );
}
