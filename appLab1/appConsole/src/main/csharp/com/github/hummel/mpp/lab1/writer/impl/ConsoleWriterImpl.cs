namespace com.github.hummel.mpp.lab1;

public class ConsoleWriterImpl : IWriter
{
    public void write(string content)
    {
        Console.WriteLine(content);
    }
}