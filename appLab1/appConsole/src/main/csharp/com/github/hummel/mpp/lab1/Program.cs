namespace com.github.hummel.mpp.lab1;

class Program
{
    private static readonly TracerImpl tracer = new TracerImpl();

    public static void Main()
    {
        var thread1 = new Thread(threadOne);
        var thread2 = new Thread(threadTwo);

        thread1.Start();
        thread2.Start();

        thread1.Join();
        thread2.Join();

        var traceResult = tracer.getTraceResult();
        var writer = new ConsoleWriterImpl();

        var content1 = new TraceResultXmlSerializerImpl().serialize(traceResult);
        writer.write(content1);

        var content2 = new TraceResultJsonSerializerImpl().serialize(traceResult);
        writer.write(content2);
    }

    private static void threadOne()
    {
        tracer.startTrace();

        Thread.Sleep(200);
        Console.WriteLine("OUTER 1");

        tracer.stopTrace();
    }

    private static void threadTwo()
    {
        tracer.startTrace();

        Console.WriteLine("OUTER 2");

        threadOne();


        tracer.stopTrace();
    }
}