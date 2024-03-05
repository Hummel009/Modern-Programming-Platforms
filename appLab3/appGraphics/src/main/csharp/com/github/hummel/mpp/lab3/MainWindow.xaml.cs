namespace com.github.hummel.mpp.lab3;

using System.Windows;

public partial class MainWindow : Window
{
    DecompilerViewModel decompilerViewModel;

    public MainWindow()
    {
        decompilerViewModel = new DecompilerViewModel();
        DataContext = decompilerViewModel;
    }

    private void TreeView_SelectedItemChanged(object sender, RoutedPropertyChangedEventArgs<object> @event)
    {
        if (@event.NewValue is AssemblyNode)
        {
            decompilerViewModel.SelectedAssm = (AssemblyNode)@event.NewValue;
        }
    }
}