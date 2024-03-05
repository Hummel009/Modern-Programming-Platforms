namespace com.github.hummel.mpp.lab3;

using FluentAssertions;
using System.Reflection;
using Microsoft.VisualStudio.TestTools.UnitTesting;

#pragma warning disable CS7022

[TestClass]
public class Tests
{
    private static AssemblyModel? model;

    [TestInitialize]
    public void testInitialize()
    {
        var basePath = AppDomain.CurrentDomain.BaseDirectory;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Directory.GetParent(basePath)!.FullName;
        basePath = Path.Combine(basePath, "src", "main", "resources");
        var filePath = Path.Combine(basePath, "Example.dll");
        model = new AssemblyModel(Assembly.LoadFrom(filePath));
    }

    [TestMethod]
    public void assemlyNameTest()
    {
        model!.name.Should().Be("MPP_3_TEST");
    }

    [TestMethod]
    public void namespaceNameTest()
    {
        model!.namespaces.Count.Should().Be(1);
        model.namespaces[0].name.Should().Be("-");
    }

    [TestMethod]
    public void classesNameTest()
    {
        var classes = model!.namespaces[0].classes;
        classes.Count.Should().Be(3);
        classes[0].name.Should().Be("Program");
        classes[1].name.Should().Be("TestClass");
        classes[2].name.Should().Be("MyExtensions");
    }

    [TestMethod]
    public void methodSignatureTest()
    {
        var testClass = model!.namespaces[0].classes[1];
        testClass.methods.Count.Should().Be(2);
        testClass.methods[0].ToString().Should().Be("protected aasd(ref Int32 asdas, in Int32 n, out Int32 a, Int32[] b = null, params Int32[] p) : Int32");
    }

    [TestMethod]
    public void propertySignatureTest()
    {
        var testClass = model!.namespaces[0].classes[1];
        testClass.properties.Count.Should().Be(1);
        testClass.properties[0].ToString().Should().Be("protected internal Int32 prop { internal get; set; }");
    }

    [TestMethod]
    public void fieldSignatureTest()
    {
        var testClass = model!.namespaces[0].classes[1];
        testClass.fields.Count.Should().Be(1);
        testClass.fields[0].ToString().Should().Be("public field : Dictionary<Int32, List<Dictionary<Int32, Int32>>>");
    }

    [TestMethod]
    public void eventSignatureTest()
    {
        var testClass = model!.namespaces[0].classes[1];
        testClass.events.Count.Should().Be(1);
        testClass.events[0].ToString().Should().Be("protected Notify{ add {} remove {} } : AccountHandler");
    }

    [TestMethod]
    public void innerClassSignatureTest()
    {
        var testClass = model!.namespaces[0].classes[1];
        testClass.innerClasses.Count.Should().Be(2);
        testClass.innerClasses[0].name.Should().Be("InnerClass");
        testClass.innerClasses[1].name.Should().Be("AccountHandler");
        var testClass2 = testClass.innerClasses[0];
        testClass2.innerClasses.Count.Should().Be(1);
        testClass2.innerClasses[0].name.Should().Be("En");
    }

    [TestMethod]
    public void extensionSignatureTest()
    {
        var testClass = model!.namespaces[0].classes[1];
        testClass.methods.Count.Should().Be(2);
        testClass.methods[1].ToString().Should().Be("extended from MyExtensions public static WordCount(TestClass str) : Int32");
    }
}