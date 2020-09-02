import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

//Runs the VRP solution. Either constructs random locations given a number, or constructs locations given a file specification
public class VehicleRoutingProblem implements Runnable
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new VehicleRoutingProblem());
    }

    public void run()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        JMenu debug = new JMenu("Debug");
        menuBar.add(debug);

        JMenuItem fileRead = new JMenuItem("Open File");
        fileRead.addActionListener(e->
        {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON", "json");
            chooser.setFileFilter(filter);

            int returnValue = chooser.showOpenDialog(null);
            if(returnValue == JFileChooser.APPROVE_OPTION)
            {
                Utilities.readSpecification(chooser.getSelectedFile());
            }
        });
        menu.add(fileRead);

        JMenuItem fileWrite = new JMenuItem("Write Demo File");
        fileWrite.addActionListener(e->
        {
            Utilities.writeDemoSpecification();
        });
        debug.add(fileWrite);

        JFrame VRPFrame = new JFrame("Vehicle Routing Problem");

        VRPFrame.setJMenuBar(menuBar);
        VRPFrame.setSize(400, 400);
        VRPFrame.setLayout(new BorderLayout());
        VRPFrame.setVisible(true);
    }
}
