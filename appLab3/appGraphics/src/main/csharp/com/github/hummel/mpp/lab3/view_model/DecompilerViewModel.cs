namespace com.github.hummel.mpp.lab3;

using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Reflection;
using System.Runtime.CompilerServices;

#pragma warning disable CS8625
#pragma warning disable CS8612
#pragma warning disable CS0169
#pragma warning disable CS8618

public class DecompilerViewModel : INotifyPropertyChanged
{
    private string sourceString = "D:/Source/Modern-Programming-Platforms/appLab3/appUnitTests/src/main/resources/example.dll";
    public string SourceString
    {
        get
        {
            return sourceString;
        }
        set
        {
            if (sourceString != value)
            {
                sourceString = value;
                OnPropertyChanged(nameof(SourceString));
            }
        }
    }

    private AssemblyNode selectedAsm;
    public AssemblyNode SelectedAsm
    {
        get
        {
            return selectedAsm;
        }
        set
        {
            if (selectedAsm != value)
            {
                selectedAsm = value;
            }
        }
    }

    private RelayCommand addCommand;
    public RelayCommand AddCommand
    {
        get
        {
            return addCommand ??= new RelayCommand(obj =>
              {
                  string path = obj as string ?? throw new NullReferenceException("Path is null");
                  Assembly? a = null;
                  try
                  {
                      a = Assembly.LoadFrom(path);
                  }
                  catch (Exception)
                  {
                      sourceString = "File not found";
                      OnPropertyChanged(nameof(SourceString));
                      return;
                  }
                  AssemblyNode assembly = new AssemblyNode(new AssemblyModel(a));
                  if (this.assemblies.Where(asm => asm.name == assembly.name).ToList().Count == 0)
                  {
                      this.assemblies.Add(assembly);
                  }
                  OnPropertyChanged(nameof(Assemblies));
              });
        }
    }

    private RelayCommand removeCommand;
    public RelayCommand RemoveCommand
    {
        get
        {
            return removeCommand ??= new RelayCommand(obj =>
              {
                  if (selectedAsm != null)
                  {
                      this.Assemblies.Remove(selectedAsm);
                      OnPropertyChanged(nameof(Assemblies));
                  }
                  selectedAsm = null;
              });
        }
    }

    public RelayCommand RefreshCommand
    {
        get
        {
            return removeCommand ??
              (removeCommand = new RelayCommand(obj =>
              {
                  if (selectedAsm != null)
                  {
                      string path = selectedAsm.path;
                      this.Assemblies.Remove(selectedAsm);
                      Assembly a = Assembly.LoadFrom(path);
                      AssemblyNode assembly = new AssemblyNode(new AssemblyModel(a));
                      this.assemblies.Add(assembly);
                      OnPropertyChanged(nameof(Assemblies));
                  }
                  selectedAsm = null;
              }));
        }
    }

    private ObservableCollection<INode> assemblies;

    public ObservableCollection<INode> Assemblies
    {
        get
        {
            return assemblies;
        }
        set
        {
            if (assemblies != value)
            {
                assemblies = value;
                OnPropertyChanged(nameof(Assemblies));
            }
        }
    }

    public DecompilerViewModel()
    {
        this.assemblies = new ObservableCollection<INode>();
    }

    public void add(AssemblyModel a) { this.Assemblies.Add(new AssemblyNode(a)); }

    public event PropertyChangedEventHandler PropertyChanged;
    public void OnPropertyChanged([CallerMemberName] string prop = "")
    {
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(prop));
    }
}

public interface INode : INotifyPropertyChanged
{
    public string name
    {
        get;
    }
}

public class AssemblyNode : INode
{
    public AssemblyNode(AssemblyModel assemly)
    {
        this.name = assemly.name;
        this.path = assemly.assembly.Location;
        this.items = new List<INode>();

        foreach (var n in assemly.namespaces)
        {
            items.Add(new NamespaceNode(n));
        }
    }
    internal string path;
    public string name
    {
        get;
        set;
    }
    public List<INode> items
    {
        get;
        set;
    }

    public event PropertyChangedEventHandler PropertyChanged;
    public void OnPropertyChanged([CallerMemberName] string prop = "")
    {
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(prop));
    }
}

public class NamespaceNode : INode
{
    public NamespaceNode(NamespaceModel namespaceModel)
    {
        this.name = namespaceModel.name;
        this.items = new List<INode>();
        foreach (var item in namespaceModel.classes)
        {
            items.Add(new ClassNode(item));
        }
    }
    public string name
    {
        get;
        set;
    }
    public List<INode> items
    {
        get;
        set;
    }

    public event PropertyChangedEventHandler PropertyChanged;
    public void OnPropertyChanged([CallerMemberName] string prop = "")
    {
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(prop));
    }
}

public class ClassNode : INode
{
    public ClassNode(ClassModel cls)
    {
        this.name = cls.name;
        this.items = new List<INode>();

        if (cls.@class.IsEnum)
        {
            imagePath = "Images\\Enum.png";
        }
        else if (cls.@class.IsSubclassOf(typeof(Delegate)))
        {
            imagePath = "Images\\Delegate.png";
        }
        else if (cls.@class.IsValueType)
        {
            imagePath = "Images\\Struct.png";
        }
        else if (cls.@class.IsInterface)
        {
            imagePath = "Images\\Interface.png";
        }
        else
        {
            imagePath = "Images\\Class.png";
        }

        foreach (var item in cls.innerClasses)
        {
            items.Add(new ClassNode(item));
        }
        foreach (var item in cls.properties)
        {
            items.Add(new PropertyNode(item));
        }
        foreach (var item in cls.fields)
        {
            items.Add(new FieldNode(item));
        }
        foreach (var item in cls.constructors)
        {
            items.Add(new ConstructorNode(item));
        }
        foreach (var item in cls.methods)
        {
            items.Add(new MethodNode(item));
        }
        foreach (var item in cls.events)
        {
            items.Add(new EventNode(item));
        }
    }
    public List<INode> items
    {
        get;
        set;
    }
    public string name
    {
        get;
        set;
    }
    public string imagePath
    {
        get;
        set;
    }

    public event PropertyChangedEventHandler PropertyChanged;
    public void OnPropertyChanged([CallerMemberName] string prop = "")
    {
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(prop));
    }
}

public class EventNode : INode
{
    public EventNode(EventModel eventModel)
    {
        this.name = eventModel.ToString();
    }

    public string name
    {
        get;
        set;
    }

    public event PropertyChangedEventHandler PropertyChanged;
    public void OnPropertyChanged([CallerMemberName] string prop = "")
    {
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(prop));
    }
}

public class MethodNode : INode
{
    public MethodNode(MethodModel method)
    {
        this.name = method.ToString();
        if (method.extension)
        {
            imagePath = "Images\\ExtensionMethod.png";
        }
        else
        {
            imagePath = "Images\\Method.png";
        }
    }
    public string name
    {
        get;
        set;
    }
    public string imagePath
    {
        get;
        set;
    }

    public event PropertyChangedEventHandler PropertyChanged;
    public void OnPropertyChanged([CallerMemberName] string prop = "")
    {
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(prop));
    }
}

public class FieldNode : INode
{
    public FieldNode(FieldModel field)
    {
        this.name = field.ToString();
        if (field.field.DeclaringType!.IsEnum && !field.field.Name.Contains("value__"))
        {
            imagePath = "Images\\EnumValue.png";
        }
        else
        {
            imagePath = "Images\\Field.png";
        }
    }

    public string name
    {
        get;
        set;
    }
    public string imagePath
    {
        get;
        set;
    }

    public event PropertyChangedEventHandler PropertyChanged;
    public void OnPropertyChanged([CallerMemberName] string prop = "")
    {
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(prop));
    }
}

public class ConstructorNode : INode
{
    public ConstructorNode(ConstructorModel constructor)
    {
        this.name = constructor.ToString();
    }

    public string name
    {
        get;
        set;
    }

    public event PropertyChangedEventHandler PropertyChanged;
    public void OnPropertyChanged([CallerMemberName] string prop = "")
    {
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(prop));
    }
}

public class PropertyNode : INode
{
    public PropertyNode(PropertyModel property)
    {
        this.name = property.ToString();
    }
    
    public string name
    {
        get;
        set;
    }

    public event PropertyChangedEventHandler PropertyChanged;
    public void OnPropertyChanged([CallerMemberName] string prop = "")
    {
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(prop));
    }
}