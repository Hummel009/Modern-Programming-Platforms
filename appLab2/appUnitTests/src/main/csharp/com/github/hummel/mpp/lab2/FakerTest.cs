using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace com.github.hummel.mpp.lab2
{
    [TestClass]
    public class FakerTest
    {
        [TestMethod]
        public void CycleDetectionTest()
        {
            Action action = () =>
            {
                FakerImpl f = new FakerImpl();
                f.create<CycleTestClass>();
            };
            action.Should().Throw<Exception>();
        }

        [TestMethod]
        public void ConstructorSelectionTest()
        {
            FakerImpl f = new FakerImpl();
            ConstructorClass obj = f.create<ConstructorClass>();
            obj.check();
        }

        [TestMethod]
        public void CommonClassTest()
        {
            FakerImpl f = new FakerImpl();
            CommonClass obj = f.create<CommonClass>();
            Type type = typeof(CommonClass);
            obj.f1.Should().NotBe(float.NaN);
            obj.p1.Should().NotBe(float.NaN);
            foreach (var list in obj.list)
            {
                list.Should().NotBeNull();
                foreach (var elemet in list)
                {
                    elemet.Should().NotBeNull();
                    elemet.f1.Should().NotBe(float.NaN);
                    elemet.f2.Should().NotBe(float.NaN);
                }
            }
        }
    }
}