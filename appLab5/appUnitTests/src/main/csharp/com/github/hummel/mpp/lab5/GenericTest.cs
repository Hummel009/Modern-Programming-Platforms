namespace com.github.hummel.mpp.lab5;

using FluentAssertions;
using NUnit.Framework;

public class GenericTest
{
    [Test]
    public void genericFirstTest()
    {
        var depConfig = new DepConfig();
        depConfig.register(typeof(IService<>), typeof(ServiceImpl<>));
        depConfig.register<IRepository, RepositoryImpl>();
        var depProvider = new DependencyProvider(depConfig);
        var service = (ServiceImpl<IRepository>)depProvider.resolve<IService<IRepository>>();
        service.repository.Should().BeOfType<RepositoryImpl>();
    }

    [Test]
    public void genericSecondTest()
    {
        var depConfig = new DepConfig();
        depConfig.register<IService<IRepository>, ServiceImpl<IRepository>>();
        depConfig.register<IRepository, RepositoryImpl>();
        var depProvider = new DependencyProvider(depConfig);
        var service = (ServiceImpl<IRepository>)depProvider.resolve<IService<IRepository>>();
        service.repository.Should().BeOfType<RepositoryImpl>();
    }
}