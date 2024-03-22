namespace com.github.hummel.mpp.lab5;

using com.github.hummel.mpp.lab2;
using System.Reflection;

#pragma warning disable CS8600
#pragma warning disable CS8601
#pragma warning disable CS8602
#pragma warning disable CS8604
#pragma warning disable CS8625
#pragma warning disable CS0219

public class DependencyProvider
{
    private readonly Dictionary<CustomDependency, object> depMap;
    private readonly FakerImpl faker = new();

    public DependencyProvider(DependenciesConfiguration dependencies)
    {
        var deps = dependencies.getDependencies();
        depMap = [];
        foreach (var dep in deps)
        {
            if (!dep.tImpl.IsAssignableTo(dep.tDep) || dep.tImpl.IsInterface || dep.tImpl.IsAbstract)
            {
                if (!dep.tImpl.GetInterfaces().Where(i => i.Name == dep.tDep.Name).Any())
                {
                    throw new Exception("Implementation class.");
                }
            }
            depMap.Add(dep, null);
        }
    }

    public List<TDependency> resolveAll<TDependency>()
    {
        var temp = new List<TDependency>();
        foreach (var dep in findByTypeAll(typeof(TDependency)))
        {
            object res;
            if (dep.lifeCycle == Kind.SINGLETON)
            {
                if (depMap[dep] == null)
                {
                    res = createDependency(dep);
                    depMap[dep] = res;
                }
                else
                {
                    res = depMap[dep];
                }
            }
            else
            {
                res = createDependency(dep);
                depMap[dep] = res;
            }
            temp.Add((TDependency)res);
        }
        return temp;
    }

    public IEnumerable<CustomDependency> findByTypeAll(Type t)
    {
        foreach (var dep in depMap.Keys)
        {
            if (dep.tDep == t)
            {
                yield return dep;
            }
        }
    }

    public TDependency resolve<TDependency>(string? name = null)
    {
        object res = null;
        bool replace = false;
        Type TDep = null;
        Type TImpl = null;
        CustomDependency dep;
        if (name != null)
        {
            dep = findByName(name);
        }
        else
        {
            dep = findByType(typeof(TDependency));
        }
        if (dep == null)
        {
            replace = true;
            dep = findByDepName(typeof(TDependency));
            if (dep == null)
            {
                throw new Exception("Dependency not found.");
            }

            _ = findByType(typeof(TDependency).GenericTypeArguments[0]) ?? throw new Exception("Dependency not found.");
            var tImpl = dep.tImpl.MakeGenericType(typeof(TDependency).GenericTypeArguments[0]);
            dep.tDep = typeof(TDependency);
            dep.tImpl = tImpl;
        }
        if (dep.lifeCycle == Kind.SINGLETON)
        {
            if (depMap[dep] == null)
            {
                res = createDependency(dep);
                depMap[dep] = res;
            }
            else
            {
                res = depMap[dep];
            }
        }
        else
        {
            res = createDependency(dep);
            depMap[dep] = res;
        }
        if (replace)
        {
            dep.tDep = TDep;
            dep.tImpl = TImpl;
        }
        res.GetType();
        return (TDependency)res;
    }

    private CustomDependency? findByName(string name)
    {
        foreach (var dep in depMap.Keys)
        {
            if (name != null && dep.name == name)
            {
                return dep;
            }
        }
        return null;
    }

    private CustomDependency? findByDepName(Type t)
    {
        foreach (var dep in depMap.Keys)
        {
            if (dep.tDep.Name == t.Name)
            {
                return dep;
            }
        }
        return null;
    }

    private CustomDependency? findByType(Type t)
    {
        foreach (var dep in depMap.Keys)
        {
            if (dep.tDep == t)
            {
                return dep;
            }
        }
        return null;
    }

    private object createDependency(CustomDependency dependency)
    {
        var usedTypes = new HashSet<Type>();
        object createRecursive(CustomDependency dependency)
        {
            var type = dependency.tImpl;
            if (!usedTypes.Add(type))
            {
                throw new Exception("Cyclic dependence.");
            }
            object res = null;
            var constrs = type.GetConstructors().Where(c => c.GetCustomAttributes<ConstructorAnnotation>().Any()).ToArray();
            if (constrs.Length == 0)
            {
                throw new Exception("Constructor not found.");
            }
            var constructor = constrs[0];
            var parms = constructor.GetParameters();
            var args = new List<object>();
            foreach (var param in parms)
            {
                var paramType = param.ParameterType;
                if (paramType.IsInterface || paramType.IsAbstract || paramType.IsGenericType)
                {
                    if (paramType.IsGenericType)
                    {
                        var paramGenType = paramType.GenericTypeArguments[0];
                        if (paramType.FullName.Contains("System"))
                        {
                            var listType = typeof(List<>).MakeGenericType(paramGenType);
                            try
                            {
                                args.Add(faker.createLab5(listType));
                            }
                            catch (KeyNotFoundException)
                            {
                                var genericMethod = typeof(DependencyProvider).GetMethod("ResolveAll");
                                var closedMethod = genericMethod.MakeGenericMethod(paramGenType);
                                var t = closedMethod.Invoke(this, null);
                                args.Add(t);
                            }
                        }
                        else
                        {
                            var genericMethod = typeof(DependencyProvider).GetMethod("Resolve");
                            var closedMethod = genericMethod.MakeGenericMethod(paramType);
                            var t = closedMethod.Invoke(this, new object[] { null });
                            args.Add(t);
                        }
                    }
                    else
                    {
                        var temp = param.GetCustomAttribute<ParameterAnnotation>();
                        CustomDependency? dep;
                        if (temp != null)
                        {
                            var name = temp.Param;
                            dep = findByName(name);
                        }
                        else
                        {
                            dep = findByType(paramType);
                        }
                        if (dep == null)
                        {
                            throw new Exception("Dependency not found.");
                        }
                        args.Add(createRecursive(dep));
                    }
                }
                else
                {
                    var temp = dependency.parameters.Where(p => p.parameterName == param.Name).ToList();
                    if (temp.Count > 0)
                    {
                        args.Add(temp[0].parameterValue);
                    }
                    else
                    {
                        args.Add(faker.createLab5(paramType));
                    }
                }
            }
            return constructor.Invoke([.. args]);
        }
        return createRecursive(dependency);
    }
}