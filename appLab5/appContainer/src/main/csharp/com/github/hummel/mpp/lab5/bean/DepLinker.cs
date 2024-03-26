namespace com.github.hummel.mpp.lab5;

public class DepLinker(Type depType, Type implType, Mode kind, string? name, List<DepParameter> depParameters)
{
    public Type depType = depType;
    public Type implType = implType;
    public Mode kind = kind;
    public string? name = name;
    public List<DepParameter> depParameters = depParameters;
}