namespace com.github.hummel.mpp.lab5;

public class CustomDependency(Type depType, Type implType, Kind kind, string? name, List<DependencyParameter> depParams)
{
    public Type depType = depType;
    public Type implType = implType;
    public Kind kind = kind;
    public string? name = name;
    public List<DependencyParameter> depParams = depParams;
}