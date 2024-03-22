namespace com.github.hummel.mpp.lab5;

using FluentAssertions;
using NUnit.Framework;

public class GenericTest
{
    [Test]
    public void genericFirstTest()
    {
        var depConfig = new DependenciesConfiguration();
        depConfig.register(typeof(IService<>), typeof(ServiceImpl<>));
        depConfig.register<IRepository, RepositoryImpl>();
        var depProvider = new DependencyProvider(depConfig);
        var service = (ServiceImpl<IRepository>)depProvider.resolve<IService<IRepository>>();
        service.repository.Should().BeOfType<RepositoryImpl>();
    }

    [Test]
    public void genericSecondTest()
    {
        var depConfig = new DependenciesConfiguration();
        depConfig.register<IService<IRepository>, ServiceImpl<IRepository>>();
        depConfig.register<IRepository, RepositoryImpl>();
        var depProvider = new DependencyProvider(depConfig);
        var service = (ServiceImpl<IRepository>)depProvider.resolve<IService<IRepository>>();
        service.repository.Should().BeOfType<RepositoryImpl>();
    }

    [Test]
    public void resolveAllTest()
    {
        var depConfig = new DependenciesConfiguration();
        depConfig.register<IService, ServiceImpl1S>();
        depConfig.register<IRepository, RepositoryImpl>();
        depConfig.register<IService, ServiceImpl2S>();
        var depProvider = new DependencyProvider(depConfig);
        var services = depProvider.resolveAll<IService>();
        services.Should().HaveCount(2);
        services[0].Should().BeOfType<ServiceImpl1S>();
        services[1].Should().BeOfType<ServiceImpl2S>();
    }
}