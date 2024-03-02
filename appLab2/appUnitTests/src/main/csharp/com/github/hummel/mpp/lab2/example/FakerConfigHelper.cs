namespace com.github.hummel.mpp.lab2
{
    public class TestClass
    {
        public int f1; 
        public int f2;
        public string f3;

        public TestClass1 f4;

        public int proc() { return 42; }
    }

    public class TestClass1
    {
        public int f1;

        public TestClass f2;
    }

    public class GenInt : ICustomGenerator<int>
    {
        public int generate() { return 42; }
    }

    public class GenString : ICustomGenerator<string>
    {
        public string generate() { return "42"; }
    }

    public class GenTestClass1 : ICustomGenerator<TestClass1>
    {
        public TestClass1 generate()
        {
            TestClass1 obj = new TestClass1();
            obj.f1 = 42;
            obj.f2 = null;
            return obj;
        }
    }
}
