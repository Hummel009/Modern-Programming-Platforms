using System.Xml;
using YAXLib;

namespace Tracer.Serialization.Xml;

public class TraceResultXmlSerializerImpl : ITraceResultSerializer
{
    public string serialize(TraceResult traceResult)
    {
        var traceResultTo = new TraceResultXmlTo(traceResult);
        
        using Stream stream = new MemoryStream();
        
        var settings = new XmlWriterSettings(){ Indent = true, IndentChars = "    ", OmitXmlDeclaration = true };
 
        using XmlWriter writer = XmlWriter.Create(stream, settings);

        var serializer = new YAXSerializer(typeof(TraceResultXmlTo));
        serializer.Serialize(traceResultTo, writer);
        
        writer.Flush();
        
        var streamReader = new StreamReader(stream);
        stream.Seek(0, SeekOrigin.Begin);
        
        var result = streamReader.ReadToEnd();
        return result.Replace(" />", "/>");
    }

    public TraceResult? deserialize(string content)
    {
        var serializer = new YAXSerializer(typeof(TraceResultXmlTo));
        var traceResultTo = (TraceResultXmlTo?)serializer.Deserialize(content);
        return traceResultTo?.traceResult;
    }
}