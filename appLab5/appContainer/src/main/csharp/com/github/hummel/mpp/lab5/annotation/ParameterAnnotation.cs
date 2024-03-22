namespace com.github.hummel.mpp.lab5;

[AttributeUsage(AttributeTargets.Parameter)]
public class ParameterAnnotation(string parameterName) : Attribute
{
    public string parameterName = parameterName;
}