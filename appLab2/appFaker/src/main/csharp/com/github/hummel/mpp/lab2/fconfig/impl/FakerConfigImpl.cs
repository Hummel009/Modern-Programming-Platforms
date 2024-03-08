namespace com.github.hummel.mpp.lab2;

using System.Linq.Expressions;
using System.Reflection;

public class FakerConfigImpl
{
    private Dictionary<Type, Dictionary<MemberInfo, Type>> globalConfig;

    public FakerConfigImpl() => globalConfig = new Dictionary<Type, Dictionary<MemberInfo, Type>>();

    public Dictionary<MemberInfo, Type> getConfigForType(Type type) => this.globalConfig[type];

    public void add<TClass, TField, TGenerator>(Expression<Func<TClass, TField>> fieldSelector)
    {
        var classType = typeof(TClass);
        var generatorType = typeof(TGenerator);
        var fieldType = typeof(TField);

        var member = (fieldSelector.Body is MemberExpression) ? ((MemberExpression)fieldSelector.Body).Member : throw new Exception("Not property or field");

        if (member.DeclaringType != classType)
        {
            throw new Exception("Class member");
        }

        Type[] generatorReturnedTypes;
        var iface = generatorType.GetInterface(typeof(ICustomGenerator<>).FullName!);
        if (iface != null)
        {
            generatorReturnedTypes = iface.GetGenericArguments();
        }
        else
        {
            throw new Exception("Not generator");
        }
        if (fieldType != generatorReturnedTypes[0])
        {
            throw new Exception("Generator type");
        }

        if (!globalConfig.ContainsKey(classType))
        {
            globalConfig.Add(classType, new Dictionary<MemberInfo, Type>());
        }
        
        var localConfig = globalConfig[classType];
        if (!localConfig.ContainsKey(member))
        {
            localConfig.Add(member, generatorType);
        }
        else
        {
            localConfig[member] = generatorType;
        }
    }
}