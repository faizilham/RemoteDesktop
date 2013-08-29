package faiz.rdp.server;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;


public class ServerMain {
	private Robot bot;
	private Dimension screenDimension;
	private CommandReceiver cmd;
	private JFrame frame;
	
	public static void main(String[] args){
		try {
			new ServerMain();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ServerMain() throws Exception{
		try {
			UIManager.setLookAndFeel(
			UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		try{
			bot = new Robot();
		}catch (Exception e){
			e.printStackTrace();
			return;
		}
		
		screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		final JLabel info = new JLabel("No client connected");
		
		cmd = new CommandReceiver(bot, screenDimension, info);
		cmd.start();
		
		final JButton btn = new JButton("Stop Server");
		frame = new JFrame("Remote Destop Server");
		
		//frame.setLayout(null);
		final JPanel pane = new JPanel(new GridLayout(2,1));
		
		
		
		Dimension frameDim = new Dimension(250, 80);
		Dimension margin = new Dimension(20, 10);
		
		pane.setOpaque(false);
		pane.setBounds(margin.width, margin.height, frameDim.width - 2*margin.width, frameDim.height - 2*margin.height);
		pane.add(btn);
		pane.add(info);
		
		
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exiting();
			}
		});
		
		frame.setLayout(null);
		frame.setResizable(false);
		frame.add(new JPanel());
		frame.add(pane);
		
		frame.setPreferredSize(frameDim);
		frame.pack();
		
		frame.setLocation(300, 100);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exiting();
			}
		});
		
		frame.setVisible(true);
	}
	
	private void exiting(){
		int res = JOptionPane.showConfirmDialog(frame, "Shutdown Server?", frame.getTitle(), JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION){
			cmd.shutdown();
			frame.dispose();
		}
	}
	
}
