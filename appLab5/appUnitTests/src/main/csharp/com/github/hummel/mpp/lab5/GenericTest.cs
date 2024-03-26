namespace com.github.hummel.mpp.lab5;

using FluentAssertions;
using NUnit.Framework;

public class GenericTest
{
    Impl instance;

    [OneTimeSetUp]
    public void initialize()
    {
        var depConfig = new DepConfig();
        depConfig.register<IInterface, Impl>();

        depConfig.register(typeof(INestedInterface), typeof(NestedImpl1), name: "Impl1");
        depConfig.register(typeof(INestedInterface), typeof(NestedImpl2), name: "Impl2");
        
        var provider = new DepProvider(depConfig);

        instance = (Impl) provider.resolve<IInterface>();
    }

    [Test]
    public void dependencyConstructorTest()
    {
        instance.shouldNotBeEmpty.Should().NotBe("");
        instance.shouldBeEmpty.Should().Be("");
    }

    [Test]
    public void dependencyNestedTest()
    {
        instance.nested.Should().BeOfType<NestedImpl1>();
    }
}