#pragma warning disable CS8618
#pragma warning disable CS8625
#pragma warning disable CS7022
#pragma warning disable CS0169

public class TestClass
{
    public delegate void AccountHandler(string message);
    protected event AccountHandler Notify
    {
        add
        {

        }
        remove
        {

        }
    }

    protected internal int prop
    {
        internal get;
        set;
    }

    public Dictionary<int, List<Dictionary<int, int>>> field;

    protected int aasd(ref int asdas, in int n, out int a, int[] b = null, params int[] p)
    {
        return a = 5;
    }

    public class InnerClass
    {
        int a;
        int b;
        static int s;

        public enum En
        {
            CLASS,
            FIELD
        }
    }
}

public static class MyExtensions
{
    public static int WordCount(this TestClass str)
    {
        return 5;
    }
}