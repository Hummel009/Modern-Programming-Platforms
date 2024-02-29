namespace ConsoleApp.Output;

public class FileWriterImpl : IWriter
{
    private readonly string fileName;

    public FileWriterImpl(string fileName)
    {
        this.fileName = fileName;
    }
    
    public void write(string content)
    {
        using StreamWriter sw = File.CreateText(fileName);
        
        sw.WriteLine(content);
    }
}