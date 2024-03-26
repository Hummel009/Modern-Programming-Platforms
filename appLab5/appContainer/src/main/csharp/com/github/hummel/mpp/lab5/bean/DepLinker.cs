namespace com.github.hummel.mpp.lab5;

public class DepLinker(Type depType, Type implType, Mode mode, string? name)
{
    public Type depType = depType;
    public Type implType = implType;
    public Mode mode = mode;
    public string? name = name;
    public List<DepParameter> depParameters = [];
}