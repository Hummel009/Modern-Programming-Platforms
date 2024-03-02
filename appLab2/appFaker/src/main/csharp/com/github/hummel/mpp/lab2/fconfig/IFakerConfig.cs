namespace com.github.hummel.mpp.lab2
{
    public interface IFakerConfig
    {
        void add<TClass, TField, TICustomGenerator>(Func<TClass, TField> fieldSelector);
    }
}
