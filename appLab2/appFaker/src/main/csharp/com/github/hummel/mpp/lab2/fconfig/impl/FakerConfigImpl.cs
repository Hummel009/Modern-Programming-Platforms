using System.Linq.Expressions;
using System.Reflection;

namespace com.github.hummel.mpp.lab2
{
    public class FakerConfigImpl
    {
        private Dictionary<Type, Dictionary<MemberInfo, Type>> classesConfigs;

        public FakerConfigImpl() => classesConfigs = new Dictionary<Type, Dictionary<MemberInfo, Type>>();

        public Dictionary<MemberInfo, Type> getClassConfig(Type type) => this.classesConfigs[type];

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
            var intf = generatorType.GetInterface(typeof(ICustomGenerator<>).FullName!);
            if (intf != null)
            {
                generatorReturnedTypes = intf.GetGenericArguments();
            }
            else
            {
                throw new Exception("Not generator");
            }
            if (fieldType != generatorReturnedTypes[0])
            {
                throw new Exception("Generator type");
            }

            if (!classesConfigs.ContainsKey(classType))
            {
                classesConfigs.Add(classType, new Dictionary<MemberInfo, Type>());
            }
            Dictionary<MemberInfo, Type> classConfig = classesConfigs[classType];
            if (!classConfig.ContainsKey(member))
            {
                classConfig.Add(member, generatorType);
            }
            else
            {
                classConfig[member] = generatorType;
            }
        }
    }
}
