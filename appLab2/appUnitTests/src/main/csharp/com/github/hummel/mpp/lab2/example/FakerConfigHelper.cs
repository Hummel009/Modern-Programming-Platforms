namespace com.github.hummel.mpp.lab2;

#pragma warning disable CS8618
#pragma warning disable CS8625

public class TestClass1
{
    public int f1;

    public TestClass2 f2;
}

public class TestClass2
{
    public int f1;
    public int f2;
    public string f3;

    public TestClass1 f4;

    public int proc()
    {
        return 9;
    }
}

public class GenInt : ICustomGenerator<int>
{
    public int generate()
    {
        return 9;
    }
}

public class GenString : ICustomGenerator<string>
{
    public string generate()
    {
        return "9";
    }
}

public class GenTestClass1 : ICustomGenerator<TestClass1>
{
    public TestClass1 generate()
    {
        return new TestClass1
        {
            f1 = 9,
            f2 = null
        };
    }
}