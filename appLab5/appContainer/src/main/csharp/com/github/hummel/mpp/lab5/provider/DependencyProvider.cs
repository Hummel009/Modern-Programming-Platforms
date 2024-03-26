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

    public DependencyProvider(DepConfig dependencies)
    {
        var deps = dependencies.getDependencies();
        depMap = [];
        foreach (var dep in deps)
        {
            if (!dep.implType.IsAssignableTo(dep.depType) || dep.implType.IsInterface || dep.implType.IsAbstract)
            {
                if (!dep.implType.GetInterfaces().Where(i => i.Name == dep.depType.Name).Any())
                {
                    throw new Exception("Implementation class.");
                }
            }
            depMap.Add(dep, null);
        }
    }

    public List<TDependency> resolveAll<TDependency>()
    {
        var dependencies = new List<TDependency>();
        foreach (var dep in findByTypeAll(typeof(TDependency)))
        {
            object res;
            if (dep.kind == Kind.SINGLETON)
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
            dependencies.Add((TDependency)res);
        }
        return dependencies;
    }

    public IEnumerable<CustomDependency> findByTypeAll(Type t)
    {
        foreach (var dep in depMap.Keys)
        {
            if (dep.depType == t)
            {
                yield return dep;
            }
        }
    }

    public TDependency resolve<TDependency>(string? name = null)
    {
        object res;
        bool replace = false;
        Type savedDepType = null;
        Type savedImplType = null;
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
            var implType = dep.implType.MakeGenericType(typeof(TDependency).GenericTypeArguments[0]);
            dep.depType = typeof(TDependency);
            dep.implType = implType;
        }
        if (dep.kind == Kind.SINGLETON)
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
            dep.depType = savedDepType;
            dep.implType = savedImplType;
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

    private CustomDependency? findByDepName(Type type)
    {
        foreach (var dep in depMap.Keys)
        {
            if (dep.depType.Name == type.Name)
            {
                return dep;
            }
        }
        return null;
    }

    private CustomDependency? findByType(Type type)
    {
        foreach (var dep in depMap.Keys)
        {
            if (dep.depType == type)
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
            var type = dependency.implType;
            if (!usedTypes.Add(type))
            {
                throw new Exception("Cyclic dependence.");
            }
            object res = null;
            var constructors = type.GetConstructors().Where(constructorInfo => constructorInfo.GetCustomAttributes<ConstructorAnnotation>().Any()).ToArray();
            if (constructors.Length == 0)
            {
                throw new Exception("Constructor not found.");
            }
            var constructor = constructors[0];
            var parameters = constructor.GetParameters();
            var args = new List<object>();
            foreach (var parameter in parameters)
            {
                var parameterType = parameter.ParameterType;
                if (parameterType.IsInterface || parameterType.IsAbstract || parameterType.IsGenericType)
                {
                    if (parameterType.IsGenericType)
                    {
                        var parameterGenericType = parameterType.GenericTypeArguments[0];
                        if (parameterType.FullName.Contains("System"))
                        {
                            var listType = typeof(List<>).MakeGenericType(parameterGenericType);
                            try
                            {
                                args.Add(faker.createLab5(listType));
                            }
                            catch (KeyNotFoundException)
                            {
                                var genericMethod = typeof(DependencyProvider).GetMethod("ResolveAll");
                                var closedMethod = genericMethod.MakeGenericMethod(parameterGenericType);
                                var arg = closedMethod.Invoke(this, null);
                                args.Add(arg);
                            }
                        }
                        else
                        {
                            var genericMethod = typeof(DependencyProvider).GetMethod("Resolve");
                            var closedMethod = genericMethod.MakeGenericMethod(parameterType);
                            var arg = closedMethod.Invoke(this, [null]);
                            args.Add(arg);
                        }
                    }
                    else
                    {
                        var annotation = parameter.GetCustomAttribute<ParameterAnnotation>();
                        CustomDependency? dep;
                        if (annotation != null)
                        {
                            var name = annotation.parameterName;
                            dep = findByName(name);
                        }
                        else
                        {
                            dep = findByType(parameterType);
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
                    var temp = dependency.depParameters.Where(depParam => depParam.parameterName == parameter.Name).ToList();
                    if (temp.Count > 0)
                    {
                        args.Add(temp[0].parameterValue);
                    }
                    else
                    {
                        args.Add(faker.createLab5(parameterType));
                    }
                }
            }
            return constructor.Invoke([.. args]);
        }
        return createRecursive(dependency);
    }
}