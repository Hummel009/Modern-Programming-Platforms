namespace com.github.hummel.mpp.lab3;

using System.Reflection;
using System.Runtime.CompilerServices;

public class ClassModel
{
    public Type @class
    {
        get;
        private set;
    }
    public NamespaceModel @namespace;
    public List<MethodModel> methods
    {
        get;
        private set;
    }
    public List<ConstructorModel> constructors
    {
        get;
        private set;
    }
    public List<FieldModel> fields
    {
        get;
        private set;
    }
    public List<PropertyModel> properties
    {
        get;
        private set;
    }
    public List<EventModel> events
    {
        get;
        private set;
    }
    public List<ClassModel> innerClasses;

    public string name
    {
        get;
    }
    public List<MemberModel> members
    {
        get;
    }
    public List<ClassModel> InnerClasses
    {
        get
        {
            return innerClasses;
        }
    }

    public ClassModel(Type @class, NamespaceModel @namespace)
    {
        this.@class = @class;
        this.@namespace = @namespace;
        this.innerClasses = new List<ClassModel>();
        this.name = getName();
        this.methods = new List<MethodModel>();
        this.constructors = new List<ConstructorModel>();
        this.fields = new List<FieldModel>();
        this.properties = new List<PropertyModel>();
        this.events = new List<EventModel>();
        this.members = new List<MemberModel>();
        fillConstructors();
        fillMethods();
        fillFields();
        fillProperties();
        fillMembers();
        fillEvents();
    }

    private void fillEvents()
    {
        var events = this.@class.GetEvents(BindingFlags.NonPublic | BindingFlags.DeclaredOnly | BindingFlags.Public | BindingFlags.Instance | BindingFlags.DeclaredOnly | BindingFlags.Static);
        foreach (var @event in events)
        {
            var eventModel = new EventModel(@event);
            this.events.Add(eventModel);
        }
    }

    private string getName()
    {
        var res = @class.Name;
        var types = new Type[] {};
        if (@class.IsGenericType)
        {
            types = @class.GetGenericArguments();
        }
        res = res.Replace($"`{types.Length}", "");
        for (var i = 0; i < types.Length; i++)
        {
            if (i == 0)
            {
                res += "<";
            }
            res += types[i].Name;
            if (i == types.Length - 1)
            {
                res += ">";
            }
            else
            {
                res += ", ";
            }
        }
        return res;
    }

    private void fillConstructors()
    {
        var constructors = this.@class.GetConstructors().ToList();
        foreach (var constructor in constructors)
        {
            var constructorModel = new ConstructorModel(constructor);
            this.constructors.Add(constructorModel);
        }
    }

    private void fillMembers()
    {
        foreach (var s in this.methods)
        {
            members.Add(new MemberModel(s.ToString()));
        }
        foreach (var s in this.fields)
        {
            members.Add(new MemberModel(s.ToString()));
        }
        foreach (var s in this.properties)
        {
            members.Add(new MemberModel(s.ToString()));
        }
        foreach (var s in this.constructors)
        {
            members.Add(new MemberModel(s.ToString()));
        }
    }

    private void fillMethods()
    {
        var methods = this.@class.GetMethods(BindingFlags.NonPublic | BindingFlags.DeclaredOnly | BindingFlags.Public | BindingFlags.Instance | BindingFlags.DeclaredOnly | BindingFlags.Static);
        foreach (var method in methods)
        {
            if (method.IsDefined(typeof(ExtensionAttribute), false))
            {
                this.@namespace.assembly.extensions.Add(method);
            }
            if (!method.Attributes.HasFlag(MethodAttributes.SpecialName))
            {
                var methodModel = new MethodModel(method);
                this.methods.Add(methodModel);
            }
        }
    }

    private void fillFields()
    {
        var fields = this.@class.GetFields(BindingFlags.Instance |
            BindingFlags.Static | BindingFlags.Public |
            BindingFlags.NonPublic | BindingFlags.DeclaredOnly);
        if (@class.Name.Contains("En1"))
        {
            _ = 5;
        }
        foreach (var field in fields)
        {
            if (!field.Name.Contains(">k__BackingField"))
            {
                var fieldModel = new FieldModel(field);
                this.fields.Add(fieldModel);
            }
        }
    }

    private void fillProperties()
    {
        var properties = this.@class.GetProperties(BindingFlags.Instance |
            BindingFlags.Static | BindingFlags.Public |
            BindingFlags.NonPublic | BindingFlags.DeclaredOnly);
        foreach (var property in properties)
        {
            var propertyModel = new PropertyModel(property);
            this.properties.Add(propertyModel);
        }
    }

    internal void addExtensionMethod(MethodInfo methodInfo)
    {
        var methodModel = new MethodModel(methodInfo, true);
        this.methods.Add(methodModel);
        this.members.Add(new MemberModel(methodModel.ToString()));
    }
}
