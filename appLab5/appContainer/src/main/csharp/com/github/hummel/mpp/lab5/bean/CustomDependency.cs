namespace com.github.hummel.mpp.lab5;

public class CustomDependency(Type tDep, Type tImpl, Kind lifeCycle, string? name, List<DependencyParameter> parameters)
{
    public Type tDep = tDep;
    public Type tImpl = tImpl;
    public Kind lifeCycle = lifeCycle;
    public string? name = name;
    public List<DependencyParameter> parameters = parameters;
}