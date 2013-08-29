package faiz.rdp.client;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ClientView extends JComponent {
	//shows the remote desktop
	
	private static final long serialVersionUID = 3954450588015817008L;
	
	private BufferedImage bufferedImage = null;
    private Dimension screenDimension = null;
	private JFrame frame;
	private float factor;
    
	public ClientView(float factor, JFrame frame){
		screenDimension = new Dimension(0, 0);
		bufferedImage = null;
		this.frame = frame;
		this.factor = factor;
		
	}
	
	public void initializeWindow(int screenW, int screenH){
		screenDimension.setSize(screenW * factor, screenH * factor);
		
		bufferedImage = new BufferedImage(screenDimension.width, screenDimension.height, BufferedImage.TYPE_INT_ARGB);
		
		frame.setResizable(false);
		frame.pack();
		frame.setLocation(300, 100);
		frame.setVisible(true);
	}
	
	public synchronized void refreshImage(BufferedImage buff, int x, int y, int w, int h){
		if(bufferedImage != null){
			bufferedImage.getGraphics().drawImage(buff, x, y, w, h, null);
			this.paintImmediately(0, 0, screenDimension.width, screenDimension.height);
		}
	}
	
	public void close(){
		if(bufferedImage != null){
			bufferedImage.getGraphics().fillRect(0, 0, screenDimension.width, screenDimension.height);
			this.paintImmediately(0, 0, screenDimension.width, screenDimension.height);
			JOptionPane.showMessageDialog(null, "Connection to server stopped");
			frame.dispose();
		}
	}

    @Override
    public Dimension getPreferredSize() {
        return screenDimension;
    }

    @Override
    public Dimension getMaximumSize() {
        return screenDimension;
    }

    @Override
    public Dimension getMinimumSize() {
        return screenDimension;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(bufferedImage != null) g.drawImage(bufferedImage, 0, 0, screenDimension.width, screenDimension.height, null);
    }
    
}
