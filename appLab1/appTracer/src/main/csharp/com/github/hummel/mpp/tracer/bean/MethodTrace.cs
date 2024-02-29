namespace Tracer;

public class MethodTrace
{
    private string name;

    private long milliseconds;
    
    private string className;

    private List<MethodTrace> methodTraces;

    public IReadOnlyList<MethodTrace> nestedMethodTraces => methodTraces;
    
    public MethodTrace(string name, long milliseconds, string className, List<MethodTrace> methodTraces)
    {
        this.name = name;
        this.milliseconds = milliseconds;
        this.className = className;
        this.methodTraces = methodTraces;
    }

    public List<MethodTrace> getMethodTraces() {
        return methodTraces;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public string getName() {
        return name;
    }

    public string getClassName() {
        return className;
    }

    public override bool Equals(object? obj)
    {
        if (obj == null || obj.GetType() != GetType())
        {
            return false;
        }

        MethodTrace methodTrace = (MethodTrace)obj;
        return name.Equals(methodTrace.name)
               && milliseconds == methodTrace.milliseconds
               && className.Equals(methodTrace.className)
               && methodTraces.SequenceEqual(methodTrace.methodTraces);
    }

    public override int GetHashCode()
    {
        int hash = 13;

        unchecked
        {
            hash = 7 * hash + name.GetHashCode();
            hash = 7 * hash + milliseconds.GetHashCode();
            hash = 7 * hash + className.GetHashCode();
            
            foreach (MethodTrace methodTrace in methodTraces)
            {
                hash = unchecked((7 * hash) + methodTrace.GetHashCode());
            }
        }

        return hash;
    }
}