package faiz.rdp.server;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import faiz.rdp.datapackage.CommandPackage;




public class CommandReceiver extends Thread {
	private Robot bot;
	private DatagramSocket socket;
	private boolean running;
	private Dimension screen;
	private ImageCapturer imgcap;
	private Thread infochange;
	private String info;
	private long lastupdate;
	private String clientip;
	
	public CommandReceiver(Robot bot, Dimension screen, JLabel infolabel) throws SocketException{
		super();
		this.bot = bot;
		this.screen = screen;
		this.imgcap = null;
					
		socket = new DatagramSocket(9990);
		socket.setSoTimeout(3);
		final JLabel infolbl = infolabel;
		infochange = new Thread(new Runnable() {
			@Override
			public void run() {
				infolbl.setText(info);
			}
		});
	}
	
	@Override
	public void run(){
		running = true;
		byte[] buffer = new byte[1024];
		//int n = 0;
		
		lastupdate = 0;
		
		while(running){
			try{
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				process(packet.getData());
			}catch(Exception e){
				if (System.currentTimeMillis() - lastupdate > 3000){
					changeInfo("No client connected");
					lastupdate = System.currentTimeMillis();
				}
			}
		}
	}
	
	public void process(byte[] data) throws Exception{
		CommandPackage pkg = new CommandPackage(data);
		switch(pkg.code){
		case CommandPackage.PRE_CONNECT: connectPkg(pkg); break;
		case CommandPackage.PRE_DISCONNECT: disconnectPkg(); break;
		case CommandPackage.PRE_CURSOR: cursorPkg(pkg); break;
		case CommandPackage.PRE_CLICK: clickPkg(pkg); break;
		case CommandPackage.PRE_KEY: keyPkg(pkg); break;
		case CommandPackage.PRE_ALIVE: alivePkg(); break;
		default:
		}
	}
	
	
	private void changeInfo(String info){
		this.info = info;
		SwingUtilities.invokeLater(infochange);
	}
	
	private void alivePkg(){
		lastupdate = System.currentTimeMillis();
		changeInfo("Connected with " + clientip);
	}
	
	private void disconnectPkg(){
		changeInfo("No client connected");
		if (imgcap!=null){
			imgcap.shutdown();
			imgcap = null;
		}
	}
	
	private void keyPkg(CommandPackage pkg){
		int key = pkg.intcomponent[0];
		boolean press= (int)(pkg.bytecomponent[0] & 0xFF) == 0;
		
		if (press)
			bot.keyPress(key);
		else
			bot.keyRelease(key);
	}
	
	private void clickPkg(CommandPackage pkg){
		int x = pkg.intcomponent[0];
		int y = pkg.intcomponent[1];
		
		int btn = (int)(pkg.bytecomponent[0] & 0xFF);
		int[] buttons = {InputEvent.BUTTON1_MASK,InputEvent.BUTTON2_MASK, InputEvent.BUTTON3_MASK}; 
		boolean press = (int)(pkg.bytecomponent[1] & 0xFF) == 0;
		
		bot.mouseMove(x, y);
		
		if (press)
			bot.mousePress(buttons[btn-1]);
		else
			bot.mouseRelease(buttons[btn-1]);		
	}
	
	private void cursorPkg(CommandPackage pkg) {
		int x = pkg.intcomponent[0];
		int y = pkg.intcomponent[1];
		
		bot.mouseMove(x, y);
	}
	
	
	private void connectPkg(CommandPackage pkg) throws Exception{
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < 4; i++){
			b.append(pkg.bytecomponent[i] & 0xFF);
			if(i<3) b.append(".");
		}
		clientip = b.toString();		
		
		imgcap = new ImageCapturer(screen, bot);
		
		imgcap.startServer(clientip);
		imgcap.start();
		
		changeInfo("Connected with " + clientip);
		
		CommandPackage reply = CommandPackage.buildConnectionPackage("127.0.0.1", screen.width, screen.height);
		byte[] replydata = reply.getPackage();
		DatagramPacket packet = new DatagramPacket(replydata, replydata.length, InetAddress.getByName(clientip),9991);
		
		socket.send(packet);
	}
	
	public void shutdown(){
		if (imgcap!=null){
			imgcap.shutdown();
			imgcap = null;
		}
		running = false;
		socket.close();
	}

}
