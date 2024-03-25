namespace com.github.hummel.mpp.lab4;

using System.Collections.Concurrent;
using System.Data;
using System.Threading.Tasks.Dataflow;

class Program
{
    public static void Main()
    {
        var maxDegreesOfParallelism = new int[] { 4, 4, 4 };
        
        var basePath = AppDomain.CurrentDomain.BaseDirectory;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Path.Combine(basePath, "src", "main", "resources");
        var filePath = Path.Combine(basePath, "Example.cs");
        var pathes = new string[] { filePath };
        generateTestClasses(pathes, maxDegreesOfParallelism);
    }

    static void generateTestClasses(string[] pathes, int[] maxDegreesOfParallelism)
    {
        var generator = new Generator();
        var buffer = new BufferBlock<string>();

        //макс степень параллелизма для блока чтения
        var readerOptions = new ExecutionDataflowBlockOptions
        {
            MaxDegreeOfParallelism = maxDegreesOfParallelism[0],
        };
        var reader = new TransformBlock<string, string>(read, readerOptions);

        //макс степень параллелизма для блока генерации
        var generatorOptions = new ExecutionDataflowBlockOptions
        {
            MaxDegreeOfParallelism = maxDegreesOfParallelism[1],
        };
        var transformer = new TransformBlock<string, ConcurrentDictionary<string, string>>(generator.generateTestClasses, generatorOptions);

        //макс степень параллелизма для блока записи
        var writerOptions = new ExecutionDataflowBlockOptions
        {
            MaxDegreeOfParallelism = maxDegreesOfParallelism[2],
        };
        var writer = new ActionBlock<ConcurrentDictionary<string, string>>(write, generatorOptions);

        buffer.LinkTo(reader);
        reader.LinkTo(transformer);
        transformer.LinkTo(writer);

        buffer.Completion.ContinueWith(task => reader.Complete());
        reader.Completion.ContinueWith(task => transformer.Complete());
        transformer.Completion.ContinueWith(task => writer.Complete());

        foreach (var path in pathes)
        {
            buffer.Post(path);
        }

        buffer.Complete();

        writer.Completion.Wait();
    }

    static async Task<string> read(string path)
    {
        return await File.ReadAllTextAsync(path);
    }

    static async Task write(ConcurrentDictionary<string, string> map)
    {
        var folder = Directory.CreateDirectory("tests");
        foreach (var entry in map)
        {
            if (folder.EnumerateFiles().Where(f => f.Name == entry.Key).ToList().Count == 1)
            {
                throw new Exception();
            }
            var copyNumber = 1;
            var fileName = $"tests\\{entry.Key}.cs";
            while (File.Exists(fileName))
            {
                fileName = $"tests\\{entry.Key}({copyNumber++}).cs";
            }
            var file = File.Create(fileName);
            var stream = new StreamWriter(file);
            await stream.WriteLineAsync(entry.Value);
            await stream.FlushAsync();
            stream.Close();
        }

    }
}