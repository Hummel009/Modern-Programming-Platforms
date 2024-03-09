namespace com.github.hummel.mpp.lab2;

public class Program
{
    public static void Main(){ 
        var faker = new FakerImpl();
        var filledObj = faker.create<FillableClass>();
        Console.WriteLine(filledObj.getI());
    }
}

class FillableClass(int i)
{
    public int getI() {
        return i;
    }
}