// See https://aka.ms/new-console-template for more information
using com.github.hummel.mpp.lab5;
using System.Runtime.Intrinsics.Arm;

DependenciesConfiguration dependenciesConfiguration = new DependenciesConfiguration();
/*dependenciesConfiguration.Register<B,A>();
dependenciesConfiguration.Register<IService, ServiceImpl1>(name: "f1");*/
//dependenciesConfiguration.Register(typeof(IService<>), typeof(ServiceImpl<>));
//dependenciesConfiguration.Register<IRepository, RepositoryImpl1>();
//dependenciesConfiguration.Register<IService, ServiceImpl>(name: "f2");
//dependenciesConfiguration.Register<IService<IRepository>, ServiceImpl<IRepository>>();
DependencyProvider dependencyProvider = new DependencyProvider(dependenciesConfiguration);

//var s = dependencyProvider.Resolve<B>();
//var services = dependencyProvider.ResolveAll<IService>().ToList();
//var services2 = dependencyProvider.Resolve<IService<IRepository>>();
//var service1 = dependencyProvider.Resolve<IService>(name: "f1");
 
_ = 5;
/*public class A : B {
    string name;
    [DependencyConstructor]
    public A(List<IService> name) { }
    public A(int x) { }
}

public abstract class B { }


public interface IService { }
public class ServiceImpl : IService
{
    IRepository r;
    [DependencyConstructor]
    public ServiceImpl(IRepository repository) { r = repository; }
}
public class ServiceImpl1 : IService
{
    IRepository r;
    [DependencyConstructor]
    public ServiceImpl1(IRepository repository) { r = repository; }
}

public interface IRepository { }
public class RepositoryImpl1 : IRepository
{
    [DependencyConstructor]
    public RepositoryImpl1() { }
}
public class RepositoryImpl : IRepository
{
    [DependencyConstructor]
    public RepositoryImpl() { }
}


interface IService<TRepository> where TRepository : IRepository { }

class ServiceImpl<TRepository> : IService<TRepository> where TRepository : IRepository
{
    TRepository r;
    [DependencyConstructor]
    public ServiceImpl(TRepository repository) { r = repository; }
}*/