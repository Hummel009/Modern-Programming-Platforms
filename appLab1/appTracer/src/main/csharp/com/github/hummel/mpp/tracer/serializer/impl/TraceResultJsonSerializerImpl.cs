using System.Xml;

namespace Tracer.Serialization.Json;

using System.Runtime.Serialization.Json;
using System.Text;

public class TraceResultJsonSerializerImpl : ITraceResultSerializer
{
    public string serialize(TraceResult traceResult)
    {
        var traceResultTo = new TraceResultJsonTo(traceResult); 
        
        using Stream stream = new MemoryStream();

        using var writer = JsonReaderWriterFactory.CreateJsonWriter(stream, Encoding.UTF8, true, true, "    ");

        var dataContractJsonSerializer = new DataContractJsonSerializer(typeof(TraceResultJsonTo));
        dataContractJsonSerializer.WriteObject(writer, traceResultTo);
        writer.Flush();

        var streamReader = new StreamReader(stream);
        stream.Seek(0, SeekOrigin.Begin);

        return streamReader.ReadToEnd();
    }

    public TraceResult? deserialize(string content)
    {
        var bytes = Encoding.UTF8.GetBytes(content);
        using XmlDictionaryReader reader = JsonReaderWriterFactory.CreateJsonReader(
                bytes,
                0,
                bytes.Length,
                Encoding.UTF8,
                new XmlDictionaryReaderQuotas(),
                null
                );

        var serializer = new DataContractJsonSerializer(typeof(TraceResultJsonTo));
        var traceResultTo = (TraceResultJsonTo?)serializer.ReadObject(reader);

        return traceResultTo?.traceResult;
    }
}