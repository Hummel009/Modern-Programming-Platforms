namespace com.github.hummel.mpp.lab3;

using System.Reflection;

public class FieldModel
{
    public FieldInfo field
    {
        get;
        private set;
    }

    public FieldModel(FieldInfo field)
    {
        this.field = field;
    }

    public override string ToString()
    {
        var res = "";
        res += setModifier(this.field.Attributes);
        res += setKeywords(this.field.Attributes);
        res += this.field.Name;
        res += " : ";
        if (this.field.FieldType.IsGenericType)
        {
            res += createGenericTypeString(this.field.FieldType);
        }
        else
        {
            res += this.field.FieldType.Name;
        }
        return res;
    }

    private string setKeywords(FieldAttributes fieldAttributes)
    {
        var res = "";
        if (fieldAttributes.HasFlag(FieldAttributes.Static))
        {
            res += "static ";
        }
        return res;
    }

    private string setModifier(FieldAttributes fieldAttributes)
    {
        var res = "";
        if (fieldAttributes.HasFlag(FieldAttributes.FamORAssem))
        {
            res += "protected internal ";
        }
        else if (fieldAttributes.HasFlag(FieldAttributes.Public))
        {
            res += "public ";
        }
        else if (fieldAttributes.HasFlag(FieldAttributes.Family))
        {
            res += "protected ";
        }
        else if (fieldAttributes.HasFlag(FieldAttributes.Assembly))
        {
            res += "internal ";
        }
        else if (fieldAttributes.HasFlag(FieldAttributes.Private))
        {
            res += "private ";
        }
        else if (fieldAttributes.HasFlag(FieldAttributes.FamANDAssem))
        {
            res += "private protected ";
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
