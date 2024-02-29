namespace Tracer;

public class ThreadTrace
{
    private int threadId;
    
    private long time;

    private List<MethodTrace> methodTraces;

    public IReadOnlyList<MethodTrace> nestedMethodTraces => methodTraces;

    public ThreadTrace(int threadId, long time, List<MethodTrace> methodTraces)
    {
        this.threadId = threadId;
        this.time = time;
        this.methodTraces = methodTraces;
    }

    public int getThreadId() {
        return threadId;
    }

    public long getTime() {
        return time;
    }

    public List<MethodTrace> getMethodTraces() {
        return methodTraces;
    }

    public override bool Equals(object? obj)
    {
        if (obj == null || obj.GetType() != typeof(ThreadTrace))
        {
            return false;
        }

        ThreadTrace threadTrace = (ThreadTrace)obj;
        return threadId == threadTrace.threadId
               && time == threadTrace.time
               && nestedMethodTraces.SequenceEqual(threadTrace.nestedMethodTraces);
    }

    public override int GetHashCode()
    {
        int hash = 13;

        unchecked
        {
            hash = 7 * hash + threadId;
            hash = 7 * hash + time.GetHashCode();
            
            foreach (MethodTrace methodTrace in nestedMethodTraces)
            {
                hash = unchecked((7 * hash) + methodTrace.GetHashCode());
            }
        }

        return hash;
    }
}