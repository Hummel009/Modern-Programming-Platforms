namespace com.github.hummel.mpp.lab5;

public class DependenciesConfiguration
{
    private readonly List<CustomDependency> dependencies = [];

    public void register<TDependency, TImplementation>(Kind lifeCycle = Kind.INSTANCE_PER_DEPENDENCY, string? name = null, params DependencyParameter[] parameters)
    {
        var dep = typeof(TDependency);
        var impl = typeof(TImplementation);
        if (dependencies.Where(d => d.tImpl == impl && d.name == name).ToList().Count == 1)
        {
            throw new Exception($"{dep.Name} with name {name} already exists");
        }
        dependencies.Add(new CustomDependency(dep, impl, lifeCycle, name, [.. parameters]));
    }

    public void register(Type TDependency, Type TImplementation, Kind lifeCycle = Kind.INSTANCE_PER_DEPENDENCY, string? name = null, params DependencyParameter[] parameters)
    {
        var dep = TDependency;
        var impl = TImplementation;
        if (dependencies.Where(d => d.tDep == dep && d.name == name).ToList().Count == 1)
        {
            throw new Exception($"{dep.Name} with name {name} already exists");
        }
        dependencies.Add(new CustomDependency(dep, impl, lifeCycle, name, [.. parameters]));
    }

    public List<CustomDependency> getDependencies() => new(dependencies);
}