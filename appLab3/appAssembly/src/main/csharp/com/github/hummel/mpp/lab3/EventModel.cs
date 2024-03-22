namespace com.github.hummel.mpp.lab3;

using System.Reflection;

public class EventModel
{
    internal EventInfo @event;

    public EventModel(EventInfo eventModel)
    {
        this.@event = eventModel;
    }

    public override string ToString()
    {
        var res = "";
        res += setModifier(@event.AddMethod!.Attributes);
        res += setKeywords();
        res += @event.Name;
        res += setProps(@event);
        res += " : ";
        res += @event.EventHandlerType!.Name;
        return res;
    }

    private string setModifier(MethodAttributes methodAttributes)
    {
        var res = "";
        if (methodAttributes.HasFlag(MethodAttributes.FamORAssem))
        {
            res += "protected internal ";
        }
        else if (methodAttributes.HasFlag(MethodAttributes.Public))
        {
            res += "public ";
        }
        else if (methodAttributes.HasFlag(MethodAttributes.Family))
        {
            res += "protected ";
        }
        else if (methodAttributes.HasFlag(MethodAttributes.Assembly))
        {
            res += "internal ";
        }
        else if (methodAttributes.HasFlag(MethodAttributes.Private))
        {
            res += "private";
        }
        else if (methodAttributes.HasFlag(MethodAttributes.FamANDAssem))
        {
            res += "private protected ";
        }
        return res;
    }

    private string setKeywords()
    {
        if (@event.AddMethod != null && @event.AddMethod.Attributes.HasFlag(MethodAttributes.Static))
        {
            return "static ";
        }
        if (@event.RemoveMethod != null && @event.RemoveMethod.Attributes.HasFlag(MethodAttributes.Static))
        {
            return "static ";
        }
        return "";
    }

    private string setProps(EventInfo ev)
    {
        var res = "{ add {} remove {} }";
        return res;
    }
}