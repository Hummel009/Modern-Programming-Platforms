namespace com.github.hummel.mpp.lab5;

using FluentAssertions;
using NUnit.Framework;

public class ExceptionTest
{
    [Test]
    public void constructorExceptionTest()
    {
        var action = () =>
        {
            var depConfig = new DepConfig();
            depConfig.register<IConstructor, Constructor>();
            var depProvider = new DependencyProvider(depConfig);
            depProvider.resolve<IConstructor>();
        };
        action.Should().Throw<Exception>();
    }

    [Test]
    public void dependencyExceptionTest()
    {
        var action = () =>
        {
            var depConfig = new DepConfig();
            var depProvider = new DependencyProvider(depConfig);
            depProvider.resolve<IConstructor>();
        };
        action.Should().Throw<Exception>();
    }

    [Test]
    public void implementionExceptionTest()
    {
        var action = () =>
        {
            var depConfig = new DepConfig();
            depConfig.register<IConstructor, ConstructorNoIface>();
            var depProvider = new DependencyProvider(depConfig);
            depProvider.resolve<IConstructor>();
        };
        action.Should().Throw<Exception>();
    }
}