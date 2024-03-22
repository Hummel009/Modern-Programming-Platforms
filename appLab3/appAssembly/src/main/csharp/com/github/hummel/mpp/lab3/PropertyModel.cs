namespace com.github.hummel.mpp.lab3;

using System;
using System.Linq;
using System.Reflection;

public class PropertyModel
{
    internal PropertyInfo property;
    private string[] modifiers = [];

    public PropertyModel(PropertyInfo property)
    {
        this.property = property;
    }

    public override string ToString()
    {
        this.modifiers = new string[2];
        var res = "";
        res += "<modifier> ";
        res += setKeywords();
        if (this.property.PropertyType.IsGenericType)
        {
            res += createGenericTypeString(this.property.PropertyType);
        }
        else
        {
            res += this.property.PropertyType.Name;
        }
        res += " ";
        res += property.Name;
        res += setProps(property);
        formatString(ref res);
        return res;
    }

    private void formatString(ref string res)
    {
        string modifier;
        if (this.modifiers.Contains("public "))
        {
            modifier = "public";
        }
        else if (this.modifiers.Contains("protected internal "))
        {
            modifier = "protected internal";
        }
        else if (this.modifiers.Contains("protected "))
        {
            modifier = "protected";
        }
        else if (this.modifiers.Contains("internal "))
        {
            modifier = "internal";
        }
        else if (this.modifiers.Contains("private protected "))
        {
            modifier = "private protected";
        }
        else
        {
            modifier = "private";
        }
        if (modifiers[0] == (modifier + " "))
        {
            modifiers[0] = "";
        }
        else
        {
            modifiers[1] = "";
        }
        res = res.Replace("<modifier>", modifier);
        res = res.Split('{')[0];
        modifiers[0] = modifiers[0].Trim();
        modifiers[1] = modifiers[1].Trim();
        if (!(modifiers[0] == ""))
        {
            modifiers[0] += " ";
        }
        if (!(modifiers[1] == ""))
        {
            modifiers[1] += " ";
        }
        res += $"{{ {modifiers[0]}get; {modifiers[1]}set; }}";
    }

    private string setProps(PropertyInfo property)
    {
        var res = "";
        res += " {";
        res += setGetter(property.GetMethod);
        res += setSetter(property.SetMethod);
        res += "}";
        return res;
    }

    private string setSetter(MethodInfo? setter)
    {
        if (setter == null)
        {
            return "";
        }
        var res = "";
        this.modifiers[1] += setModifier(setter.Attributes);
        res += this.modifiers[1];
        res += "set; ";
        return res;
    }

    private string setGetter(MethodInfo? getter)
    {
        if (getter == null)
        {
            return "";
        }
        var res = "";
        this.modifiers[0] += setModifier(getter.Attributes);
        res += this.modifiers[0];
        res += "get; ";
        return res;
    }

    private string setModifier(MethodAttributes attributes)
    {
        var res = "";
        if (attributes.HasFlag(MethodAttributes.FamORAssem))
        {
            res += "protected internal ";
        }
        else if (attributes.HasFlag(MethodAttributes.Public))
        {
            res += "public ";
        }
        else if (attributes.HasFlag(MethodAttributes.Family))
        {
            res += "protected ";
        }
        else if (attributes.HasFlag(MethodAttributes.Assembly))
        {
            res += "internal ";
        }
        else if (attributes.HasFlag(MethodAttributes.Private))
        {
            res += "private";
        }
        else if (attributes.HasFlag(MethodAttributes.FamANDAssem))
        {
            res += "private protected ";
        }
        return res;
    }

    private string setKeywords()
    {
        if (property.SetMethod != null && property.SetMethod.Attributes.HasFlag(MethodAttributes.Static))
        {
            return "static ";
        }
        else if (property.GetMethod != null && property.GetMethod.Attributes.HasFlag(MethodAttributes.Static))
        {
            return "static ";
        }
        return "";
    }

    private string createGenericTypeString(Type type)
    {
        var res = "";
        var len = type.Name.ElementAt(type.Name.IndexOf('`') + 1) - '0';
        var a = type.GetGenericTypeDefinition();
        if (a != null)
        {
            res += a.Name;
            var types = type.GenericTypeArguments;
            while (res.Contains('`'))
            {
                var s = "";
                for (var i = 0; i < len; i++)
                {
                    if (i == 0)
                    {
                        s = $"<{types[i].Name}";
                    }
                    else
                    {
                        s += $", {types[i].Name}";
                    }
                }
                s += ">";
                foreach (var tmp in types)
                {
                    if (tmp.IsGenericType)
                    {
                        s = s.Replace(tmp.Name, createGenericTypeString(tmp));
                    }
                }
                res = res.Replace($"`{len}", s);
            }
        }
        return res;
    }
}
