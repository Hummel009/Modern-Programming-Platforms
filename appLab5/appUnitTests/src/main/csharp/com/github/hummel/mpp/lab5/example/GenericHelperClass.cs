namespace com.github.hummel.mpp.lab5;

[method: ConstructorAnnotation]
public class ServiceImpl<TRepository>(TRepository repository) : IService<TRepository> where TRepository : IRepository
{
    public TRepository repository = repository;
}

[method: ConstructorAnnotation]
public class RepositoryImpl() : IRepository { }

[method: ConstructorAnnotation]
public class ServiceImpl1S() : IService { }

[method: ConstructorAnnotation]
public class ServiceImpl2S() : IService { }

[method: ConstructorAnnotation]
public class ServiceImpl1T(IEnumerable<IService> services) : ITest
{
    public IEnumerable<IService> services = services;
}

[method: ConstructorAnnotation]
public class ServiceImpl2T(IService<IRepository> service) : ITest
{
    public IService<IRepository> service = service;
}

public interface IRepository { }
public interface IService { }
public interface ITest { }
public interface IService<TRepository> where TRepository : IRepository { }