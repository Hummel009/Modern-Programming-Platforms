namespace com.github.hummel.mpp.lab5;

#pragma warning disable CS8618

public class Impl : IInterface
{
    public readonly string shouldNotBeEmpty = "";
    public readonly string shouldBeEmpty = "";
    
    public readonly INestedInterface nested;

    public Impl()
    {
    }

    [ConstructorAnnotation]
    public Impl(
        INestedInterface nested,
        string shouldNotBeEmpty
    )
    {
        this.nested = nested;
        this.shouldNotBeEmpty = shouldNotBeEmpty;
    }
}

public class NestedImpl1 : INestedInterface
{
    [ConstructorAnnotation]
    public NestedImpl1()
    {
    }
}

public class NestedImpl2 : INestedInterface
{
    [ConstructorAnnotation]
    public NestedImpl2()
    {
    }
}

public interface IInterface
{
}

public interface INestedInterface
{
}