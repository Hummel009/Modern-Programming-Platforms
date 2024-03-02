namespace com.github.hummel.mpp.lab1;

using YAXLib.Attributes;
using YAXLib.Enums;

[YAXSerializeAs("root")]
public class TraceResultXmlTo
{
    [YAXCollection(YAXCollectionSerializationTypes.RecursiveWithNoContainingElement, EachElementName = "thread")]
    public List<ThreadTraceXmlTo> threadTraces
    { get; set; }

    public TraceResultXmlTo(TraceResult traceResult)
    {
        threadTraces = new List<ThreadTraceXmlTo>();
        foreach (ThreadTrace threadTrace in traceResult.nestedThreadTraces)
        {
            threadTraces.Add(new ThreadTraceXmlTo(threadTrace));
        }
    }

    public TraceResultXmlTo()
    {
        threadTraces = new List<ThreadTraceXmlTo>();
    }

    internal TraceResult traceResult =>
        new(
            (from threadTrace in threadTraces
             select threadTrace.threadTrace).ToList()
        );
}