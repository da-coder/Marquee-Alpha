package marquee;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class JDisplayPanel extends JPanel {
    String output = "";
    int x = 0, y = 0, count = 0;
    double outputWidth;
    String[] array = {""};
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString(output, x, y+30);
        
        //get string width
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect2D = fm.getStringBounds(output, g);
        outputWidth = rect2D.getWidth();
        
        g.dispose();
    }
    
    public void setArrayText(String[] input) {
        array = input;
    }
    
    public void setTextCount(int input) {
        count = input;
    }
    
    public void updateText() {
        output = array[count];
    }
    
    public void setTextLocation(int xin, int yin) {
        x = xin;
        y = yin;
    }
    
    public int getTextWidth() {
        return (int) outputWidth;
    }
}