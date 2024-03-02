namespace com.github.hummel.mpp.lab2;

using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;

#pragma warning disable CS0436

[TestClass]
public class FakerConfigTest
{
    [TestMethod]
    public void classMemberExceptionTest()
    {
        var action = () =>
        {
            FakerConfigImpl fakerConfig = new FakerConfigImpl();
            TestClass1 t = new TestClass1();
            fakerConfig.add<TestClass, int, GenInt>(test => t.f1);
        };
        action.Should().Throw<Exception>();
    }

    [TestMethod]
    public void notPropertyOrFieldExceptionTest()
    {
        var action = () =>
        {
            FakerConfigImpl fakerConfig = new FakerConfigImpl();
            fakerConfig.add<TestClass, int, GenInt>(test => test.proc());
        };
        action.Should().Throw<Exception>();
    }

    [TestMethod]
    public void notGeneratorExceptionTest()
    {
        var action = () =>
        {
            FakerConfigImpl fakerConfig = new FakerConfigImpl();
            fakerConfig.add<TestClass, int, TestClass1>(test => test.f1);
        };
        action.Should().Throw<Exception>();
    }

    [TestMethod]
    public void generatorTypeExceptionTest()
    {
        var action = () =>
        {
            FakerConfigImpl fakerConfig = new FakerConfigImpl();
            fakerConfig.add<TestClass, int, GenString>(test => test.f1);
        };
        action.Should().Throw<Exception>();
    }
}