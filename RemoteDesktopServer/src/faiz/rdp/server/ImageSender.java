package faiz.rdp.server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class ImageSender {
	private DatagramSocket socket;
	private String clientip;
	
	public ImageSender(String clientip){
		this.clientip = clientip;
		
		try {			
			socket = new DatagramSocket(9991);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown(){
		socket.close();
	}
	
	public void send(byte[] data){
		try{
			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(clientip),9990);
			if(socket!=null) socket.send(packet);
			//System.out.println(packet.getSocketAddress());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
