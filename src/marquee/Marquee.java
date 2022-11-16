//Sean Cameron Slater @ 2018-12-29

package marquee;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

public class Marquee implements KeyListener {
    
    JFrame frame;
    JLabel imagePane;
    JLabel overlayPane;
    JTextArea status;
    JLayeredPane layer;
    
    JDisplayPanel scrollingTextPanel;
    
    int displayWidth = 0;
    int count = 1;
    int scrollSpeed = 2;
    
    Marquee() {
        frame = new JFrame();
        scrollingTextPanel = new JDisplayPanel();
        imagePane = new JLabel();
        overlayPane = new JLabel();
        status = new JTextArea();
        layer = new JLayeredPane();
        
        imagePane.setBounds(0,0,1920,1080);
        overlayPane.setBounds(0,0,1920,1080);
        overlayPane.setOpaque(false);
        status.setBounds(20,816,280,512);
        status.setOpaque(false);
        status.setEditable(false);
        status.setForeground(Color.white);
        status.setFont(new Font("Arial", Font.PLAIN, 24));
        status.setText("loading system status...");
        scrollingTextPanel.setBounds(0,1040,1920,40);
        scrollingTextPanel.setOpaque(false);
        
        frame.setLayout(new BorderLayout());
        frame.add(layer, BorderLayout.CENTER);
            layer.add(imagePane, new Integer(0));
            layer.add(overlayPane, new Integer(1));
            layer.add(scrollingTextPanel, new Integer(2));
            layer.add(status, new Integer(2));
            
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.addKeyListener(this);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setBackground(new Color(0,0,0,0));
        //frame.setOpacity(0.0f);
        frame.setTitle("Marquee_AlphaBuild");
        frame.setVisible(true);
        
        displayWidth = frame.getWidth();
    }
    
    private void updateImage(String path, JLabel object) throws Exception {
        URL overlay = new URL(path);
        Image image = ImageIO.read(overlay);
        object.setIcon(new ImageIcon(image));
    }
    
    private String[] getText() throws Exception {
        String[] text = {""};
        
        //status text -> text[0]
        URL status = new URL("https://students.uta.edu/sc/scs8098/status.txt");
        Scanner s = new Scanner(status.openStream());
        text[0] = s.nextLine();
        s.close();
        
        //marquee text
        URL marqueeLines = new URL("https://students.uta.edu/sc/scs8098/marquee.txt");
        Scanner ml = new Scanner(marqueeLines.openStream());

        //count lines
        String temp;
        int i = 1;
        while(ml.hasNextLine()) {
            ml.nextLine();
            i++;
        }
        ml.close();

        //add lines to array
        URL marqueeRead = new URL("https://students.uta.edu/sc/scs8098/marquee.txt");
        Scanner mr = new Scanner(marqueeRead.openStream());
        //save status from reforming string[]
        temp = text[0];
        text = new String[i];
        text[0] = temp;
        for (int j=1; j<i; j++) {
            text[j] = mr.nextLine();
        }
        mr.close();
        
        return text;
    }
    
    private void setText(String[] text) {
        //set text line
        if (count >= text.length) {
            count = 1;
        }
        scrollingTextPanel.setTextCount(count);
        
        //send text
        scrollingTextPanel.setArrayText(text);
        
        //update status text
        String outputText = "System Status:\n"
                + "\n"
                + "Canvas:\tx\n"
                + "MyMav:\tx\n"
                + "Office 365:\tx\n"
                + "TEST:\tx\n"
                + "Network:\tx\n";
        for (int i=0; i<5; i++) {
            switch (text[0].charAt(i)) {
                case '0':
                    outputText = outputText.replaceFirst("x", "Loading");
                    break;
                case '1':
                    outputText = outputText.replaceFirst("x", "Online");
                    break;
                case '2':
                    outputText = outputText.replaceFirst("x", "Limited");
                    break;
                default:
                    outputText = outputText.replaceFirst("x", "Offline");
                    break;
            }
        }
        status.setText(outputText);
        
        //color status
        Highlighter highlighter = status.getHighlighter();
        HighlightPainter blackPaint = new DefaultHighlighter.DefaultHighlightPainter(Color.black);
        HighlightPainter redPaint = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
        HighlightPainter yellowPaint = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow.darker());
        HighlightPainter greenPaint = new DefaultHighlighter.DefaultHighlightPainter(Color.green);
        
        try {
            int start = 0, end = 0;
            for (int i=0; i<5; i++) {
                if ((start = outputText.indexOf("Loading", end)) != -1) {
                    end = start + "Loading".length();
                    highlighter.addHighlight(start, end, blackPaint);
                }
            }
            end = 0;
            for (int i=0; i<5; i++) {
                if ((start = outputText.indexOf("Offline", end)) != -1) {
                    end = start + "Offline".length();
                    highlighter.addHighlight(start, end, redPaint);
                }
            }
            end = 0;
            for (int i=0; i<5; i++) {
                if ((start = outputText.indexOf("Limited", end)) != -1) {
                    end = start + "Limited".length();
                    highlighter.addHighlight(start, end, yellowPaint);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR SETTING COLORS");
        }
    }
    
    public void updateStuff() {
        //get and update scrolling text
        try {
            setText(getText());
        } catch (Exception exception) {
            System.out.println("ERROR READING TEXT FILE...");
            exception.printStackTrace();
        }

        //get and update image
        try {
            updateImage("https://students.uta.edu/sc/scs8098/overlay_alpha.png", overlayPane);
        } catch (Exception exception) {
            System.out.println("ERROR READING IMAGE FILE...");
        }
    }
    
    public void Run() {
        //display loading stuff
        String text[] = {"00000", "- OIT help desk - hold space to speed up scroll - marquee alpha build - do not distribute -"};
        setText(text);
        
        Thread render = new Thread(new Runnable() {
            @Override
            public void run() {
                int x = 0, y = 0;
                
                while(true) {
                    x -= scrollSpeed;
                    
                    int width = scrollingTextPanel.getTextWidth();
                    if (x < -width) {
                        x = displayWidth;
                        count++;
                        scrollingTextPanel.setTextLocation(x, y);
                        scrollingTextPanel.updateText();
                        
                        //update stuff
                        updateStuff();
                    } else {
                        scrollingTextPanel.setTextLocation(x, y);
                        scrollingTextPanel.repaint();
                    }
                    
                    //System.out.println("Painting @ " + x + ", " + y);
                    
                    try {
                        Thread.sleep(1000/30);
                    } catch (Exception e) {
                        System.out.println("ERROR WHILE WAITING...");
                    }
                }
            }
        });
        render.start();
    }
    
    public static void main(String[] args) {
        Marquee m = new Marquee();
        m.Run();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 32)
            scrollSpeed = 32;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 32)
            scrollSpeed = 2;
    }
}