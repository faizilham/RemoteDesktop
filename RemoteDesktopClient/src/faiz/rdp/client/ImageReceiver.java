package faiz.rdp.client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import faiz.rdp.datapackage.ImagePackage;



public class ImageReceiver extends Thread {
	private ClientView client;
	private boolean running = true;
	private DatagramSocket socket;
	private long lastupdate;
	
	public ImageReceiver(ClientView client){
		super();
		try {
			socket = new DatagramSocket(9990);
			socket.setSoTimeout(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.client = client;
	}
	
	public void initScreenSize(int screenW, int screenH){
		client.initializeWindow(screenW, screenH);
	}

	@Override
	public void run() {
		byte[] buffer = new byte[65000];
		lastupdate = System.currentTimeMillis();
		//int n = 0;
		while(running){
			try{
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				lastupdate = System.currentTimeMillis();
				new ImageProcessor(packet.getData()).start();
				
			}catch(Exception e){
				if (System.currentTimeMillis() - lastupdate > 5000){
					shutdown();
					client.close();
				}
			}
			
			//if (n > 2) running = false;
		}
	}
	
	public void shutdown(){
		running = false;
		socket.close();
	}
	
	private class ImageProcessor extends Thread{
		ImagePackage img;
		
		public ImageProcessor(byte[] rawdata){
			super();
			try {
				img = new ImagePackage(rawdata);
			} catch (IOException e) {
				System.out.println("socket closed");
			}
		}
		
		public void run(){
			client.refreshImage(img.buildImage(), img.index_x*ImagePackage.SLICE_WIDTH, img.index_y*ImagePackage.SLICE_HEIGHT, img.width, img.height);
		}
		
	}
}
