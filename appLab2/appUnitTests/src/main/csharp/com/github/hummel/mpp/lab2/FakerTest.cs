namespace com.github.hummel.mpp.lab2;

[TestClass]
public class FakerTest
{
    [TestMethod]
    public void defaultClassTest()
    {
        var faker = new FakerImpl();
        var obj = faker.create<ExampleClass>();
        obj.amogus.Should().NotBe(float.NaN);
        obj.sus.Should().NotBe(float.NaN);
        foreach (var list in obj.list)
        {
            list.Should().NotBeNull();
            foreach (var item in list)
            {
                item.Should().NotBeNull();
                item.amogus.Should().NotBe(float.NaN);
                item.sus.Should().NotBe(float.NaN);
            }
        }
    }

    [TestMethod]
    public void recursiveDetectionTest()
    {
        var action = () =>
        {
            var faker = new FakerImpl();
            faker.create<RecursiveClass1>();
        };
        action.Should().Throw<Exception>();
    }
}