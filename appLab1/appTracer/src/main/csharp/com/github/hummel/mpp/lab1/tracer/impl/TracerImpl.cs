namespace com.github.hummel.mpp.lab1;

using System.Collections.Concurrent;
using System.Diagnostics;
using System.Reflection;

public class TracerImpl : ITracer
{
    private ConcurrentDictionary<int, IList<MetricsEngine>> threadEnginesMap
    {
        get;
    } = new();

    private ConcurrentDictionary<int, List<MethodTrace>> threadTracesMap
    {
        get;
    } = new();

    private MethodBase getCurrentMethodBase()
    {
        var stackTrace = new StackTrace(2); //начинаем с 2, т.к. 1 - это текущий метод
        var stackFrame = stackTrace.GetFrame(0); //первый фрейм - это и есть нужный метод
        if (stackFrame == null)
        {
            throw new Exception("Method stack trace is null");
        }

        var methodBase = stackFrame.GetMethod();
        if (methodBase == null)
        {
            throw new Exception("Method base is null");
        }

        return methodBase;
    }

    public void startTrace()
    {
        var threadId = Environment.CurrentManagedThreadId;

        if (!threadEnginesMap.ContainsKey(threadId))
        {
            threadEnginesMap[threadId] = new List<MetricsEngine>();
            threadTracesMap[threadId] = new List<MethodTrace>();
        }

        var methodBase = getCurrentMethodBase();
        var engine = new MetricsEngine(methodBase);
        engine.startTimer();
        threadEnginesMap[threadId].Add(engine);
    }

    public void stopTrace()
    {
        var threadId = Thread.CurrentThread.ManagedThreadId;

        var enginesList = threadEnginesMap[threadId];
        var lastEngineInd = enginesList.Count - 1;
        var lastEngine = enginesList[lastEngineInd];
        lastEngine.stopTimer();

        enginesList.RemoveAt(lastEngineInd);
        var currMethodTrace = lastEngine.MethodTrace;

        if (lastEngineInd == 0)
        {
            threadTracesMap[threadId].Add(currMethodTrace);
        }
        else
        {
            var parent = enginesList[lastEngineInd - 1];
            parent.addMethodTrace(currMethodTrace);
        }
    }

    public TraceResult getTraceResult()
    {
        var threadTracesInfos = new List<ThreadTrace>();
        var timeCounter = new TimeCounter();

        foreach (var mapEntry in threadTracesMap)
        {
            var time = timeCounter.count(mapEntry.Value);
            var threadTrace = new ThreadTrace(mapEntry.Key, time, mapEntry.Value);
            threadTracesInfos.Add(threadTrace);
        }

        return new TraceResult(threadTracesInfos);
    }

    public long count(IEnumerable<MethodTrace> methodTraces)
    {
        var res = 0L;

        foreach (var methodTrace in methodTraces)
        {
            res += methodTrace.getMilliseconds();
            res += count(methodTrace.nestedMethodTraces);
        }

        return res;
    }
}