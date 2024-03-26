namespace com.github.hummel.mpp.lab5;

public class DepConfig
{
    private readonly List<Linker> linkers = [];

    public List<Linker> getDependencies() => new(linkers);

    public void register<DepType, ImplType>(Kind kind = Kind.INSTANCE_PER_DEPENDENCY, string? name = null, params DependencyParameter[] parameters)
    {
        register(typeof(DepType), typeof(ImplType), kind, name, parameters);
    }

    public void register(Type depType, Type implType, Kind kind = Kind.INSTANCE_PER_DEPENDENCY, string? name = null, params DependencyParameter[] parameters)
    {
        if (linkers.Where(d => d.depType == depType && d.name == name).ToList().Count == 1)
        {
            throw new Exception($"Choose another name when registering implementations.");
        }
        linkers.Add(new Linker(depType, implType, kind, name, [.. parameters]));
    }
}