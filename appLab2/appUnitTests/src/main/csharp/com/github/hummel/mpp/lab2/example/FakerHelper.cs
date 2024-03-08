namespace com.github.hummel.mpp.lab2;

#pragma warning disable CS8618

public class RecursiveClass1
{
    public RecursiveClass2 cycled;
}

public class RecursiveClass2
{
    public RecursiveClass1 test;
}

public class ExampleClass
{
    public float amogus = float.NaN;

    public float sus { get; set; } = float.NaN;

    public List<List<ExampleClassInner>> list;
}

public class ExampleClassInner
{
    public float amogus = float.NaN;
    public float sus = float.NaN;
}
