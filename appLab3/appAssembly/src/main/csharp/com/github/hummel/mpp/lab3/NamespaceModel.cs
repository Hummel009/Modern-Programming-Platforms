namespace com.github.hummel.mpp.lab3;

#pragma warning disable CS8618

public class NamespaceModel
{
    public string name
    {
        get;
    }
    public AssemblyModel assembly;
    public List<ClassModel> classes;
    public List<ClassModel> unmodClasses
    {
        get
        {
            return classes;
        }
    }

    public NamespaceModel(string name)
    {
        this.name = name;
        this.classes = new List<ClassModel>();
    }

    public NamespaceModel(string name, AssemblyModel assemblyModel)
    {
        this.assembly = assemblyModel;
        this.name = name;
        this.classes = new List<ClassModel>();
    }

    public NamespaceModel(string name, List<ClassModel> classModels)
    {
        this.name = name;
        this.classes = classModels;
    }

    public void addClass(Type @class)
    {
        this.classes.Add(new ClassModel(@class, this));
    }

    public bool replaceClasses(ref List<ClassModel> cl, ClassModel cm)
    {
        if (cm.@class.DeclaringType == null)
        {
            return false;
        }
        foreach (var c in cl)
        {
            if (c.innerClasses.Count > 0)
            {
                replaceClasses(ref c.innerClasses, cm);
            }
            if (c.name == cm.@class.DeclaringType.Name)
            {
                if (!c.InnerClasses.Contains(cm))
                {
                    c.innerClasses.Add(cm);
                }
                return true;
            }
        }
        return false;
    }
}