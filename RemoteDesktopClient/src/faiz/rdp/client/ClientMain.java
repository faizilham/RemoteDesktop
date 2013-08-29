package faiz.rdp.client;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import faiz.rdp.datapackage.ImagePackage;


public class ClientMain {
	public static void main(String[] args){
		new ClientMain();
		
	}
	
	private static final float factor = ImagePackage.factor;
	
	//private C
	/*
	 * Enumeration<?> e=NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements())        {
            NetworkInterface n=(NetworkInterface) e.nextElement();
            Enumeration<?> ee = n.getInetAddresses();
            while(ee.hasMoreElements())
            {
                InetAddress i= (InetAddress) ee.nextElement();
                String ip = i.getHostAddress();
                if(!ip.contains(":") && !i.isLoopbackAddress())
                	System.out.println(i.getHostAddress());
            }
        }
	 */
	
	private ClientMain(){
		try {
			UIManager.setLookAndFeel(
			UIManager.getSystemLookAndFeelClassName());			
		} catch (Exception e) {}
		
		String clientip = null;
		
		try{
			ArrayList<String> ipaddress = new ArrayList<String>();
			
			Enumeration<?> e=NetworkInterface.getNetworkInterfaces();
	        while(e.hasMoreElements())        {
	            NetworkInterface n=(NetworkInterface) e.nextElement();
	            Enumeration<?> ee = n.getInetAddresses();
	            while(ee.hasMoreElements())
	            {
	                InetAddress i= (InetAddress) ee.nextElement();
	                String ip = i.getHostAddress();
	                if(!ip.contains(":") && !i.isLoopbackAddress())
	                	ipaddress.add(ip);
	            }
	        }
	        
	        if (ipaddress.isEmpty()) throw new Exception();
	        
	        Object[] iplist = ipaddress.toArray();
		    Object sel = JOptionPane.showInputDialog(null,"Your IP address", "Remote Desktop Client",JOptionPane.INFORMATION_MESSAGE, null,iplist, iplist[0]);
			
		    if (sel == null) return;
		    clientip = (String) sel;
		    
		} catch (Exception e){
			JOptionPane.showMessageDialog(null, "Connection Error");
			return;
		}
		
		
	     


		//request server ip address
		
		String serverip = JOptionPane.showInputDialog(null, "Server IP address", "Remote Desktop Client", JOptionPane.OK_CANCEL_OPTION);
		
		if (serverip == null || serverip.equals("")) return;
		
		
		final JFrame frame = new JFrame("Remote Desktop Client - " + serverip);
		final ClientView client = new ClientView(factor, frame);
		
		final ImageReceiver imgrcv = new ImageReceiver(client);
		final CommandSender cmd = new CommandSender(imgrcv, serverip, clientip);
		
		
		client.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				int x = (int) (arg0.getX() / factor);
				int y = (int) (arg0.getY() / factor);
				cmd.sendClick(arg0.getButton(), 1, x, y);
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				int x = (int) (arg0.getX() / factor);
				int y = (int) (arg0.getY() / factor);
				cmd.sendClick(arg0.getButton(), 0, x, y);
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});
		
		client.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
				int x = (int) (arg0.getX() / factor);
				int y = (int) (arg0.getY() / factor);
				cmd.sendCursorPos(x, y);
			}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				int x = (int) (arg0.getX() / factor);
				int y = (int) (arg0.getY() / factor);
				cmd.sendCursorPos(x, y);
			}
		});
		
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				cmd.sendKey(arg0.getKeyCode(), 1);
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				cmd.sendKey(arg0.getKeyCode(), 0);
			}
		});
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				imgrcv.shutdown();
				cmd.shutdown();
				frame.dispose();
			}
		});
		
		cmd.start();
		
		frame.add(client);
	
	}
}
