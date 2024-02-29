namespace ConsoleApp.Output;

public class ConsoleWriterImpl : IWriter
{
    public void write(string content)
    {
        Console.WriteLine(content);
    }
}