namespace com.github.hummel.mpp.lab5;

public class DepConfig
{
    private readonly List<CustomDependency> dependencies = [];

    public List<CustomDependency> getDependencies() => new(dependencies);

    public void register<DepType, ImplType>(Kind kind = Kind.INSTANCE_PER_DEPENDENCY, string? name = null, params DependencyParameter[] parameters)
    {
        register(typeof(DepType), typeof(ImplType), kind, name, parameters);
    }

    public void register(Type depType, Type implType, Kind kind = Kind.INSTANCE_PER_DEPENDENCY, string? name = null, params DependencyParameter[] parameters)
    {
        if (dependencies.Where(d => d.depType == depType && d.name == name).ToList().Count == 1)
        {
            throw new Exception($"Choose another name when registering implementations.");
        }
        dependencies.Add(new CustomDependency(depType, implType, kind, name, [.. parameters]));
    }
}