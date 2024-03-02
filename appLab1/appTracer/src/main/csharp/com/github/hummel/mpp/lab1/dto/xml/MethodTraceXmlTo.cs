namespace com.github.hummel.mpp.lab1;

using YAXLib.Attributes;
using YAXLib.Enums;

#pragma warning disable CS8618

public class MethodTraceXmlTo
{
    private long time;

    [YAXAttributeForClass]
    [YAXSerializeAs("name")]
    public string methodName
    { get; set; }

    [YAXAttributeForClass]
    [YAXSerializeAs("time")]
    public string timeValue
    {
        get => time + "ms";
        set => time = long.Parse(value[..^2]);
    }

    [YAXAttributeForClass]
    [YAXSerializeAs("class")]
    public string className
    { get; set; }

    [YAXCollection(YAXCollectionSerializationTypes.RecursiveWithNoContainingElement,
        EachElementName = "method")]
    public List<MethodTraceXmlTo> nestedTraces
    { get; set; }

    public MethodTraceXmlTo(MethodTrace methodTrace)
    {
        time = methodTrace.getMilliseconds();
        className = methodTrace.getClassName();
        methodName = methodTrace.getName();

        nestedTraces = new List<MethodTraceXmlTo>();
        foreach (MethodTrace trace in methodTrace.getMethodTraces())
        {
            nestedTraces.Add(new MethodTraceXmlTo(trace));
        }
    }

    public MethodTraceXmlTo()
    {
        nestedTraces = new List<MethodTraceXmlTo>();
    }

    internal MethodTrace methodTrace =>
        new(
            methodName,
            time,
            className,
            (from nestedTrace in nestedTraces ??= []
             select nestedTrace.methodTrace).ToList()
        );
}