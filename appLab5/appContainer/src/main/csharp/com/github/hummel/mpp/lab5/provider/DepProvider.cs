namespace com.github.hummel.mpp.lab5;

using com.github.hummel.mpp.lab2;
using System.Reflection;

#pragma warning disable CS8600
#pragma warning disable CS8601
#pragma warning disable CS8602
#pragma warning disable CS8604
#pragma warning disable CS8625
#pragma warning disable CS0219

public class DepProvider
{
    private readonly Dictionary<DepLinker, object> depMap;
    private readonly FakerImpl faker = new();

    public DepProvider(DepConfig config)
    {
        var deps = config.getDependencies();
        depMap = [];
        foreach (var dep in deps)
        {
            //тип dep.implType может быть присвоен переменной, имеющей тип dep.depType или его подтипу
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

    public List<DepType> resolveAll<DepType>()
    {
        var dependencies = new List<DepType>();
        foreach (var dep in findByTypeAll(typeof(DepType)))
        {
            object res;
            if (dep.kind == Mode.SINGLETON)
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
            dependencies.Add((DepType)res);
        }
        return dependencies;
    }

    public IEnumerable<DepLinker> findByTypeAll(Type t)
    {
        foreach (var dep in depMap.Keys)
        {
            if (dep.depType == t)
            {
                yield return dep;
            }
        }
    }

    public DepType resolve<DepType>(string? name = null)
    {
        object res;
        bool replace = false;
        Type savedDepType = null;
        Type savedImplType = null;
        DepLinker dep;
        if (name != null)
        {
            dep = findByName(name);
        }
        else
        {
            dep = findByType(typeof(DepType));
        }
        if (dep == null)
        {
            replace = true;
            dep = findByDepName(typeof(DepType));
            if (dep == null)
            {
                throw new Exception("Dependency not found.");
            }

            _ = findByType(typeof(DepType).GenericTypeArguments[0]) ?? throw new Exception("Dependency not found.");
            var implType = dep.implType.MakeGenericType(typeof(DepType).GenericTypeArguments[0]);
            dep.depType = typeof(DepType);
            dep.implType = implType;
        }
        if (dep.kind == Mode.SINGLETON)
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
        return (DepType)res;
    }

    private DepLinker? findByName(string name)
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

    private DepLinker? findByDepName(Type type)
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

    private DepLinker? findByType(Type type)
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

    private object createDependency(DepLinker dependency)
    {
        var usedTypes = new HashSet<Type>();
        object createRecursive(DepLinker dependency)
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
                                var genericMethod = typeof(DepProvider).GetMethod("ResolveAll");
                                var closedMethod = genericMethod.MakeGenericMethod(parameterGenericType);
                                var arg = closedMethod.Invoke(this, null);
                                args.Add(arg);
                            }
                        }
                        else
                        {
                            var genericMethod = typeof(DepProvider).GetMethod("Resolve");
                            var closedMethod = genericMethod.MakeGenericMethod(parameterType);
                            var arg = closedMethod.Invoke(this, [null]);
                            args.Add(arg);
                        }
                    }
                    else
                    {
                        var annotation = parameter.GetCustomAttribute<ParameterAnnotation>();
                        DepLinker? dep;
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