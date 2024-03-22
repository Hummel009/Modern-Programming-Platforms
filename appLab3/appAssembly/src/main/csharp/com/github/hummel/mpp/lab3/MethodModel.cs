namespace com.github.hummel.mpp.lab3;

using System.Reflection;

public class MethodModel
{
    internal MethodInfo method;
    public bool extension
    {
        get;
        private set;
    }

    public MethodModel(MethodInfo method, bool extension = false)
    {
        this.extension = extension;
        this.method = method;
    }

    public override string ToString()
    {
        var res = "";
        if (this.extension)
        {
            res += $"extended from {this.method.DeclaringType!.FullName} ";
        }
        res += setModifier(this.method.Attributes);
        res += setKeywords(this.method.Attributes);
        res += setGenericParams(this.method.GetGenericArguments());
        res += this.method.Name;
        res += setParams(this.method.GetParameters());
        res += " : ";
        res += setReturnType(this.method.ReturnType);
        return res;
    }

    private string setGenericParams(Type[] types)
    {
        var res = "";
        for (var i = 0; i < types.Length; i++)
        {
            if (i == 0)
            {
                res += "<";
            }
            res += types[i].Name;
            if (i == types.Length - 1)
            {
                res += ">";
            }
            else
            {
                res += ", ";
            }
        }
        return res;
    }

    private string setReturnType(Type type)
    {
        var res = "";
        if (type.IsGenericType)
        {
            res += createGenericTypeString(type);
        }
        else
        {
            res = type.Name;
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

    private string setModifier(MethodAttributes attributes)
    {
        string res = "";
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
            res += "private ";
        }
        else if (attributes.HasFlag(MethodAttributes.FamANDAssem))
        {
            res += "private protected ";
        }
        return res;
    }

    private string setKeywords(MethodAttributes attributes)
    {
        string res = "";
        if (attributes.HasFlag(MethodAttributes.Static))
        {
            res += "static ";
        }
        else if (attributes.HasFlag(MethodAttributes.Virtual | MethodAttributes.NewSlot))
        {
            res += "virtual ";
        }
        else if (attributes.HasFlag(MethodAttributes.Final | MethodAttributes.Virtual))
        {
            res += "sealed override ";
        }
        else if (attributes.HasFlag(MethodAttributes.Virtual))
        {
            res += "override ";
        }
        else if (attributes.HasFlag(MethodAttributes.Final))
        {
            res += "sealed ";
        }
        return res;
    }
}
