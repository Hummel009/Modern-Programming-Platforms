using System;
using System.Data;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using com.github.hummel.mpp.lab4;
using Moq;
using NUnit.Framework;

namespace Tests {
    [TestFixture]
    public class ExampleTests {
        private Mock<IDataReader> _dependency1;
        private Mock<ISerializable> _dependency2;
        private Mock<IObjectReference> _dependency3;
        private Example _myClassUnderTest;

        [SetUp]
        public void SetUp() {
            _dependency1 = new Mock<IDataReader>();
            _dependency2 = new Mock<ISerializable>();
            _dependency3 = new Mock<IObjectReference>();
            string param1 = default(string);
            _myClassUnderTest = new Example(_dependency1.Object, _dependency2.Object, _dependency3.Object, param1);
        }

        [Test]
        public void testTest() {
            int a = default(int);
            int b = default(int);
            int c = default(int);
            int actual = _myClassUnderTest.test(a, b, c);
            int expected = default(int);
            Assert.That(actual, Is.EqualTo(expected));
            Assert.Fail("autogenerated");
        }

        [Test]
        public void test1Test() {
            Assert.DoesNotThrow(() => { _myClassUnderTest.test1(); });
            Assert.Fail("autogenerated");
        }

        [Test]
        public void test2Test() {
            Assert.DoesNotThrow(() => { _myClassUnderTest.test2(); });
            Assert.Fail("autogenerated");
        }
    }
}
