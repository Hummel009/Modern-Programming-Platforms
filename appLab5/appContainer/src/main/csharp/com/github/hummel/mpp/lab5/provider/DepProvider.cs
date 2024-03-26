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
    private readonly Dictionary<DepLinker, object> linkerObjMap;
    private readonly FakerImpl faker = new();

    public DepProvider(DepConfig config)
    {
        var linkers = config.getLinkers();
        linkerObjMap = [];
        foreach (var linker in linkers)
        {
            //если имплемент не похож на имплемент по 3 критериям
            if (!linker.implType.IsAssignableTo(linker.depType) || linker.implType.IsInterface || linker.implType.IsAbstract)
            {
                //если интерфейс не подходит
                if (!linker.implType.GetInterfaces().Where(iface => iface.Name == linker.depType.Name).Any())
                {
                    throw new Exception("Implementation class.");
                }
            }
            linkerObjMap.Add(linker, null);
        }
    }

    public DepType resolve<DepType>()
    {
        DepLinker linker = findLinkerByType(typeof(DepType));
        
        if (linker == null)
        {
            linker = findLinkerByName(typeof(DepType));

            if (linker == null)
            {
                throw new Exception("Linker not found.");
            }

            var implType = linker.implType.MakeGenericType(typeof(DepType).GenericTypeArguments[0]);

            linker.depType = typeof(DepType);
            linker.implType = implType;
        }
        
        object obj; //single or not

        if (linker.mode == Mode.SINGLETON)
        {
            if (linkerObjMap[linker] == null)
            {
                obj = fabricateObj(linker);
                linkerObjMap[linker] = obj;
            }
            else
            {
                obj = linkerObjMap[linker];
            }
        }
        else
        {
            obj = fabricateObj(linker);
            linkerObjMap[linker] = obj;
        }

        return (DepType) obj;
    }

    private DepLinker? findLinkerByType(Type type)
    {
        foreach (var linker in linkerObjMap.Keys)
        {
            if (linker.depType == type)
            {
                return linker;
            }
        }
        return null;
    }

    private DepLinker? findLinkerByName(Type type)
    {
        foreach (var linker in linkerObjMap.Keys)
        {
            if (linker.depType.Name == type.Name)
            {
                return linker;
            }
        }
        return null;
    }

    private object fabricateObj(DepLinker dependency)
    {
        var usedTypes = new HashSet<Type>();

        object fabricateRecursive(DepLinker dependency)
        {
            var type = dependency.implType;

            if (!usedTypes.Add(type))
            {
                throw new Exception("Cyclic dependence from lab 2.");
            }

            object res = null;
            var neededConstructors = type.GetConstructors().Where(constructorInfo => constructorInfo.GetCustomAttributes<ConstructorAnnotation>().Any()).ToArray();
            if (neededConstructors.Length == 0)
            {
                throw new Exception("Constructor not found.");
            }

            var constructor = neededConstructors[0];
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
                        var listType = typeof(List<>).MakeGenericType(parameterGenericType);
                        args.Add(faker.createLab5(listType));
                    }
                    else
                    {
                        var linker = findLinkerByType(parameterType) ?? throw new Exception("Linker not found.");
                        args.Add(fabricateRecursive(linker));
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

        return fabricateRecursive(dependency);
    }
}