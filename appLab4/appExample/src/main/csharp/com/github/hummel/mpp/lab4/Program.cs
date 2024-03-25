namespace com.github.hummel.mpp.lab4;

using System.Collections.Concurrent;
using System.Data;
using System.Threading.Tasks.Dataflow;

class Program
{
    public static void Main()
    {
        var basePath = AppDomain.CurrentDomain.BaseDirectory;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Path.Combine(basePath, "src", "main", "resources");
        var filePath = Path.Combine(basePath, "Example.cs");
        var pathes = new string[] { filePath };
        generateTestClasses(pathes, 4, 4, 4);
    }

    static void generateTestClasses(string[] pathes, int parallel1, int parallel2, int parallel3)
    {
        var generator = new Generator();
        var bufferBlock = new BufferBlock<string>();

        //макс степень параллелизма для блока чтения
        var readerOptions = new ExecutionDataflowBlockOptions
        {
            MaxDegreeOfParallelism = parallel1,
        };
        var readerBlock = new TransformBlock<string, string>(read, readerOptions);

        //макс степень параллелизма для блока генерации
        var generatorOptions = new ExecutionDataflowBlockOptions
        {
            MaxDegreeOfParallelism = parallel2,
        };
        var generatorBlock = new TransformBlock<string, ConcurrentDictionary<string, string>>(generator.generateTestClasses, generatorOptions);

        //макс степень параллелизма для блока записи
        var writerOptions = new ExecutionDataflowBlockOptions
        {
            MaxDegreeOfParallelism = parallel3,
        };
        var writer = new ActionBlock<ConcurrentDictionary<string, string>>(write, writerOptions);

        //попадание данных в новый блок по выходу из старого - цепочка данных
        bufferBlock.LinkTo(readerBlock);
        readerBlock.LinkTo(generatorBlock);
        generatorBlock.LinkTo(writer);

        //начало выполнения нового блока по завершении старого - цепочка действий
        bufferBlock.Completion.ContinueWith(task => readerBlock.Complete());
        readerBlock.Completion.ContinueWith(task => generatorBlock.Complete());
        generatorBlock.Completion.ContinueWith(task => writer.Complete());

        //все пути к классам положить в стартовый блок данных
        foreach (var path in pathes)
        {
            bufferBlock.Post(path);
        }

        //больше данных не будет
        bufferBlock.Complete();

        //подождать завершения последнего блока - цепочка действий
        writer.Completion.Wait();
    }

    static async Task<string> read(string path)
    {
        return await File.ReadAllTextAsync(path);
    }

    static async Task write(ConcurrentDictionary<string, string> map)
    {
        var basePath = AppDomain.CurrentDomain.BaseDirectory;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Path.Combine(basePath, "src", "main", "resources");
        
        foreach (var entry in map)
        {
            var fileName = entry.Key;
            var fileContent = entry.Value;
            
            var copyNumber = 1;
            
            var filePath = Path.Combine(basePath, $"{fileName}.cs");

            while (File.Exists(filePath))
            {
                filePath = Path.Combine(basePath, $"{fileName} [{copyNumber++}].cs");
            }
            var file = File.Create(filePath);
            var stream = new StreamWriter(file);
            await stream.WriteLineAsync(fileContent);
            await stream.FlushAsync();
            stream.Close();
        }
    }
}