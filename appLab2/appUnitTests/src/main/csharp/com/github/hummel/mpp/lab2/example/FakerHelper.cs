using FluentAssertions;

namespace com.github.hummel.mpp.lab2
{
    public class CycleTestClass
    {
        public Cycled cycled;
    }

    public class Cycled
    {
        public CycleTestClass test;
    }

    public class ConstructorClass
    {
        private float f1 = float.NaN;
        public float f2 = float.NaN;
        public float f3 = float.NaN;
        public float f4 = float.NaN;

        private float p1 { get; set; } = float.NaN;
        public float p2 { get; set; } = float.NaN;
        public float p3 { get; set; } = float.NaN;
        public float p4 { get; } = float.NaN;
        public float p5 { get; private set; } = float.NaN;
        public float p6 { get; private set; } = float.NaN;

        public ConstructorClass() { }
        public ConstructorClass(float f2, float f3, float p2, float p3, float f4)
        {
            this.f2 = f2;
            this.f3 = f3;
            p4 = p4;
            this.p2 = p2;
            this.p3 = p3;
        }
        public ConstructorClass(float f1, float p1, float p4)
        {
            this.f1 = f1;
            this.p1 = p1;
            this.p4 = p4;
        }
        public ConstructorClass(float f1, float p1, float p4, float p5)
        {
            this.f1 = f1;
            this.p1 = p1;
            this.p4 = p4;
            this.p5 = p5;
        }

        public void check()
        {
            f1.Should().NotBe(float.NaN);
            p1.Should().NotBe(float.NaN);
            p4.Should().NotBe(float.NaN);
            p5.Should().NotBe(float.NaN);
            p6.Should().Be(float.NaN);
        }
    }

    public class CommonClass
    {
        public float f1 = float.NaN;

        public float p1 { get; set; } = float.NaN;

        public List<List<CommonClass2>> list;
    }

    public class CommonClass2
    {
        public float f1 = float.NaN;
        public float f2 = float.NaN;
    }
}
