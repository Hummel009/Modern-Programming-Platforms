namespace com.github.hummel.mpp.lab2;

using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;

[TestClass]
public class FakerTest
{
    [TestMethod]
    public void cycleDetectionTest()
    {
        var action = () =>
        {
            var faker = new FakerImpl();
            faker.create<CycleTestClass>();
        };
        action.Should().Throw<Exception>();
    }

    [TestMethod]
    public void constructorSelectionTest()
    {
        var faker = new FakerImpl();
        var obj = faker.create<ConstructorClass>();
        obj.check();
    }

    [TestMethod]
    public void commonClassTest()
    {
        var faker = new FakerImpl();
        var obj = faker.create<CommonClass>();
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