namespace com.github.hummel.mpp.lab3;

using System;
using System.Linq;
using System.Reflection;

public class ConstructorModel
{
    internal ConstructorInfo constructor;

    public ConstructorModel(ConstructorInfo constructor)
    {
        this.constructor = constructor;
    }

    public override string ToString()
    {
        var res = "";
        res += setModifier(constructor.Attributes);
        res += setKeywords(constructor.Attributes);
        res += this.constructor.DeclaringType!.Name;
        res += setParams(this.constructor.GetParameters());
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
            res += "private ";
        }
        else if (methodAttributes.HasFlag(MethodAttributes.FamANDAssem))
        {
            res += "private protected ";
        }
        return res;
    }

    private string setKeywords(MethodAttributes methodAttributes)
    {
        var res = "";
        if (methodAttributes.HasFlag(MethodAttributes.Static))
        {
            res += "static ";
        }
        else if (methodAttributes.HasFlag(MethodAttributes.Virtual | MethodAttributes.NewSlot))
        {
            res += "virtual ";
        }
        else if (methodAttributes.HasFlag(MethodAttributes.Final | MethodAttributes.Virtual))
        {
            res += "sealed ovveride ";
        }
        else if (methodAttributes.HasFlag(MethodAttributes.Virtual))
        {
            res += "override ";
        }
        else if (methodAttributes.HasFlag(MethodAttributes.Final))
        {
            res += "sealed ";
        }
        return res;
    }

    private string setParams(ParameterInfo[] parameters)
    {
        var res = "";
        var len = parameters.Length;
        if (len == 0)
        {
            res += "()";
        }
        for (var i = 0; i < len; i++)
        {
            if (i == 0)
            {
                res += "(";
            }
            var param = parameters[i];
            var tmp = "";
            if (param.ParameterType.IsGenericType)
            {
                tmp += createGenericTypeString(param.ParameterType);
            }
            else
            {
                tmp = param.ParameterType.Name;
            }
            if (param.Attributes.HasFlag(ParameterAttributes.In))
            {
                tmp = tmp.Replace("&", "");
                res += "in ";
                res += tmp;
            }
            else if (param.Attributes.HasFlag(ParameterAttributes.Out))
            {
                tmp = tmp.Replace("&", "");
                res += "out ";
                res += tmp;
            }
            else if (tmp.Contains('&'))
            {
                tmp = tmp.Replace("&", "");
                res += "ref ";
                res += tmp;
            }
            else if (param.ParameterType.IsArray && param.IsDefined(typeof(ParamArrayAttribute), false))
            {
                res += "params ";
                res += tmp;
            }
            else
            {
                res += tmp;
            }
            res += " ";
            res += param.Name;
            if (param.Attributes.HasFlag(ParameterAttributes.HasDefault))
            {
                res += " = ";
                var def = param.DefaultValue;
                if (def == null)
                {
                    res += "null";
                }
                else
                {
                    res += def.ToString();
                }
            }
            if (i == len - 1)
            {
                res += ")";
            }
            else
            {
                res += ", ";
            }
        }
        return res;
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