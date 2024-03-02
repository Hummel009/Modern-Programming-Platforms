using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace com.github.hummel.mpp.lab2
{
    [TestClass]
    public class FakerConfigTest
    {
        [TestMethod]
        public void ClassMemberExceptionTest()
        {
            Action action = () => { 
                FakerConfigImpl fakerConfig = new FakerConfigImpl();
                TestClass1 t = new TestClass1();
                fakerConfig.add<TestClass, int, GenInt>(test => t.f1);
            };
            action.Should().Throw<Exception>();
        }

        [TestMethod]
        public void NotPropertyOrFieldExceptionTest()
        {
            Action action = () => {
                FakerConfigImpl fakerConfig = new FakerConfigImpl();
                fakerConfig.add<TestClass, int, GenInt>(test => test.proc());
            };
            action.Should().Throw<Exception>();
        }

        [TestMethod]
        public void NotGeneratorExceptionTest()
        {
            Action action = () => {
                FakerConfigImpl fakerConfig = new FakerConfigImpl();
                fakerConfig.add<TestClass, int, TestClass1>(test => test.f1);
            };
            action.Should().Throw<Exception>();
        }

        [TestMethod]
        public void GeneratorTypeExceptionTest()
        {
            Action action = () => {
                FakerConfigImpl fakerConfig = new FakerConfigImpl();
                fakerConfig.add<TestClass, int, GenString>(test => test.f1);
            };
            action.Should().Throw<Exception>();
        }
    }
}
