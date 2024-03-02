namespace com.github.hummel.mpp.lab2
{
    public class FakerImpl : IFaker
    {
        private readonly FakerConfigImpl? config;

        public FakerImpl() => config = null;

        public FakerImpl(FakerConfigImpl config) => this.config = config;

        public T create<T>()
        {
            var type = typeof(T);
            var conf = config == null ? null : config.getClassConfig(type);
            var obj = Generators.generateDto(type, conf);
            return (T)obj;
        }
    }
}
