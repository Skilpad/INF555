import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class ImageViewer extends JFrame {
    
    public ImageViewer(GrayImage  img) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageComponent ic = new ImageComponent(img);
        add(ic);
        pack();
        setVisible(true);
    }
}

class ImageComponent extends JComponent {

    private GrayImage  img;

    public ImageComponent(GrayImage img) {
        this.img = img;
        setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
    }

    public void paint(Graphics g) {
        g.drawImage(img.toImage(), 0, 0, this);
    }
    
    // show an GrayImage img: new ImageViewer(img);
    

}