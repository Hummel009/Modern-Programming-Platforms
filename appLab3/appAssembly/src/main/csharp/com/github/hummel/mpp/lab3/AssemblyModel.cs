namespace com.github.hummel.mpp.lab3;

using System.Reflection;

public class AssemblyModel
{
    public Assembly assembly
    {
        get;
        private set;
    }
    private Dictionary<string, NamespaceModel> namespacesMap;
    internal List<MethodInfo> extensions;

    public string name
    {
        get;
    }
    public List<NamespaceModel> namespaces
    {
        get;
        set;
    }

    public AssemblyModel(Assembly assembly)
    {
        this.assembly = assembly;
        this.extensions = new List<MethodInfo>();
        this.namespacesMap = new Dictionary<string, NamespaceModel>();
        var types = assembly.GetTypes().Where(t => !t.Name.Contains("<>c")).ToArray(); ;
        foreach (var type in types)
        {
            var @namespace = type.Namespace == null ? "-" : type.Namespace;
            if (!namespacesMap.ContainsKey(@namespace))
            {
                var namespaceModel = new NamespaceModel(@namespace, this);
                namespaceModel.addClass(type);
                namespacesMap.Add(@namespace, namespaceModel);
            }
            else
            {
                namespacesMap[@namespace].addClass(type);
            }
        }
        this.namespaces = namespacesMap.Values.ToList();
        this.name = assembly.GetName().Name ?? "null";
        replaceClasses();
        placeExtensionMethods();
    }

    private void placeExtensionMethods()
    {
        foreach (var item in extensions)
        {
            var name = item.DeclaringType!.Namespace ?? "-";
            var namespaceModel = namespacesMap[name];
            var t = item.GetParameters()[0].ParameterType.Name;
            var classModel = namespaceModel.classes.Where(c => c.name == t).FirstOrDefault();
            classModel?.addExtensionMethod(item);
        }
    }

    private void replaceClasses()
    {
        var flag = true;
        while (flag)
        {
            flag = false;
            foreach (var n in namespaces)
            {
                var remove = new List<ClassModel>();
                foreach (var c in n.classes)
                {
                    if (n.replaceClasses(ref n.classes, c))
                    {
                        remove.Add(c);
                    }
                }
                foreach (var c in remove)
                {
                    n.classes.Remove(c);
                }
            }
            foreach (var n in namespaces)
            {
                foreach (var c in n.classes)
                {
                    if (c.name.Contains('+'))
                    {
                        flag = true;
                        break;
                    }
                }
                if (flag)
                {
                    break;
                }
            }
        }
    }
}