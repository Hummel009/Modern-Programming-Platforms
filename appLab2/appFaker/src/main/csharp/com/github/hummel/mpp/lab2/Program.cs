namespace com.github.hummel.mpp.lab2;

#pragma warning disable CS8618

public class Program
{
    public static void Main()
    {
        var f = new FakerImpl();
        f.create<TestClass>();
    }
}

public class Gen : ICustomGenerator<int>
{

    public int generate()
    {
        return 5;
    }
}

public class TestClass
{

    public int a;
    public int b;
    private int t;
    private int y;
    public A cA;

    public int c { get; private set; }
    public int d { private get; set; }
    private int dg { get; set; }

    public List<int> aasds;
    public int aasd() { return 5; }
    public TestClass(int a) { this.a = a; }
    public TestClass() { }
}

public class A
{

    public int a;
    public int b;

    public A() { }
}

public class B
{

    public int a;
    public int b;
    public B() { }
}