namespace com.github.hummel.mpp.lab5;

public class DepConfig
{
    private readonly List<DepLinker> linkers = [];

    public List<DepLinker> getLinkers() => new(linkers);

    public void register<DepType, ImplType>(Mode kind = Mode.INSTANCE_PER_DEPENDENCY, string? name = null)
    {
        register(typeof(DepType), typeof(ImplType), kind, name);
    }

    public void register(Type depType, Type implType, Mode kind = Mode.INSTANCE_PER_DEPENDENCY, string? name = null)
    {
        if (linkers.Where(d => d.depType == depType && d.name == name).ToList().Count == 1)
        {
            throw new Exception($"Choose another name when registering implementations.");
        }
        linkers.Add(new DepLinker(depType, implType, kind, name));
    }
}