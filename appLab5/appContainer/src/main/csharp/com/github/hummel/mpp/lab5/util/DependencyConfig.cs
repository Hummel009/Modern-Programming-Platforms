namespace com.github.hummel.mpp.lab5;

public class DepConfig
{
    private readonly List<CustomDependency> dependencies = [];

    public List<CustomDependency> getDependencies() => new(dependencies);

    public void register<TDependency, TImplementation>(Kind kind = Kind.INSTANCE_PER_DEPENDENCY, string? name = null, params DependencyParameter[] parameters)
    {
        register(typeof(TDependency), typeof(TImplementation), kind, name, parameters);
    }

    public void register(Type depType, Type implType, Kind kind = Kind.INSTANCE_PER_DEPENDENCY, string? name = null, params DependencyParameter[] parameters)
    {
        if (dependencies.Where(d => d.depType == depType && d.name == name).ToList().Count == 1)
        {
            throw new Exception($"{depType.Name} with name {name} already exists");
        }
        dependencies.Add(new CustomDependency(depType, implType, kind, name, [.. parameters]));
    }
}