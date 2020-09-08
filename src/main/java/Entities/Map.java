package Entities;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

//JPanel to represent the routes and locations.
public class Map extends JPanel
{
    public Location[] locations;

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponents(g);
        this.setBackground(Color.WHITE);

        Graphics2D g2D = (Graphics2D) g;

        // draw depot
        g2D.setColor(Color.RED);
        g2D.fillRect(locations[0].getX(), locations[0].getY(), 4, 4);
        // draw locations
        g2D.setColor(Color.BLACK);
        for(int i = 1; i < locations.length; i++)
        {
            g2D.fillRect(locations[i].getX(), locations[i].getY(), 4, 4);
        }
    }
}
