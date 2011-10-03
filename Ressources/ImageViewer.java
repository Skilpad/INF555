import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class ImageViewer extends JFrame {
    
    public ImageViewer(Image img) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageComponent ic = new ImageComponent(img);
        add(ic);
        pack();
        setVisible(true);
    }
}

class ImageComponent extends JComponent {

    private Image img;

    public ImageComponent(Image img) {
        this.img = img;
        setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
    }

    public void paint(Graphics g) {
        g.drawImage(img.toImage(), 0, 0, this);
    }
}