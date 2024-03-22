namespace com.github.hummel.mpp.lab5;

[AttributeUsage(AttributeTargets.Parameter)]
public class ParameterAnnotation(string key) : Attribute
{
    public string Param { get; } = key;
}