namespace com.github.hummel.mpp.lab2;

using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;

[TestClass]
public class FakerConfigTest
{
    [TestMethod]
    public void notGeneratorExceptionTest()
    {
        var action = () =>
        {
            var fakerConfig = new FakerConfigImpl();
            fakerConfig.add<TestClass2, int, TestClass1>(test => test.f1);
        };
        action.Should().Throw<Exception>();
    }

    [TestMethod]
    public void generatorTypeExceptionTest()
    {
        var action = () =>
        {
            var fakerConfig = new FakerConfigImpl();
            fakerConfig.add<TestClass2, int, GenString>(test => test.f1);
        };
        action.Should().Throw<Exception>();
    }

    [TestMethod]
    public void classMemberExceptionTest()
    {
        var action = () =>
        {
            var fakerConfig = new FakerConfigImpl();
            var testClass1 = new TestClass1();
            fakerConfig.add<TestClass2, int, GenInt>(test => testClass1.f1);
        };
        action.Should().Throw<Exception>();
    }

    [TestMethod]
    public void notPropertyOrFieldExceptionTest()
    {
        var action = () =>
        {
            var fakerConfig = new FakerConfigImpl();
            fakerConfig.add<TestClass2, int, GenInt>(test => test.proc());
        };
        action.Should().Throw<Exception>();
    }
}