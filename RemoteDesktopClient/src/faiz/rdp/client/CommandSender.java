package faiz.rdp.client;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import faiz.rdp.datapackage.CommandPackage;



public class CommandSender extends Thread{
	private DatagramSocket socket;
	private ImageReceiver imgrcv;
	private String serverip, clientip;
	private boolean running;
	
	public CommandSender(ImageReceiver imgrcv, String serverip, String clientip){
		super();
		
		this.serverip = serverip;
		this.clientip = clientip;
		
		try {			
			socket = new DatagramSocket(9991);
			this.imgrcv = imgrcv;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private CommandPackage send(CommandPackage pkg, boolean receive){
		byte[] data = pkg.getPackage();
		try{
			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(serverip),9990);
			if(socket!=null){
				
				socket.send(packet);
				if (receive){
					byte[] buffer = new byte[1024];
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
					socket.setSoTimeout(10);
					socket.receive(reply);
					
					return new CommandPackage(reply.getData());
				}
			}
			
			return null;
		}catch(Exception e){
			//e.printStackTrace();
			return null;
		}
		
	}
	
	private void send(CommandPackage pkg){
		send(pkg, false);
	}
	
	public void sendCursorPos(int x, int y){
		send(CommandPackage.buildCursorPackage(x, y));
	}
	
	public void sendClick(int button, int state, int x, int y){
		send(CommandPackage.buildClickPackage(button, state, x, y));
	}
	
	public void sendKey(int key, int state){
		send(CommandPackage.buildKeyPackage(key, state));
	}
	
	public void run(){
		running = true;
		CommandPackage reply = send(CommandPackage.buildConnectionPackage(clientip, 0, 0), true);
		
		if(reply==null) return;
		
		imgrcv.initScreenSize(reply.intcomponent[0], reply.intcomponent[1]);
		imgrcv.start();
		
		while (running){
			send(CommandPackage.buildKeepAlivePackage());
			try{
				Thread.sleep(300);
			}catch(Exception e){}
		}
	}
	
	public void shutdown(){
		send(CommandPackage.buildDisconnectPackage());
		
		running = false;
	}
}
