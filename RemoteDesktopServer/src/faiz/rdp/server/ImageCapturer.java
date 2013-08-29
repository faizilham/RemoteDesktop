package faiz.rdp.server;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;

import faiz.rdp.datapackage.ImagePackage;

import net.coobird.thumbnailator.Thumbnails;


public class ImageCapturer extends Thread {
	private static final int sliceWidth = ImagePackage.SLICE_WIDTH, sliceHeight = ImagePackage.SLICE_HEIGHT;
	
	private boolean running;
	private Rectangle screenRect;
	private Dimension resized;
	private Robot bot;
	private String clientip;
	
	public ImageCapturer(Dimension screenDimension, Robot bot){
		super();
		this.bot = bot;
		screenRect = new Rectangle(screenDimension);
		
		float factor = ImagePackage.factor;
		
		resized = new Dimension((int)(screenDimension.width * factor), (int)(screenDimension.height * factor));
	}
	
	public void shutdown(){
		running = false;
	}
	
	public void startServer(String clientip){
		this.clientip = clientip;
	}
	
	
	public void run(){
		ImageSender sender = new ImageSender(clientip);
		running = true;
		
		while (running){
			BufferedImage shot;
			try {
				shot = Thumbnails.of(bot.createScreenCapture(screenRect)).size(resized.width, resized.height).asBufferedImage();
				System.gc();
			}catch (IOException e) {
				e.printStackTrace();
				continue;
			}
				
			
			for (int i = 0; i <= resized.width / sliceWidth; i++){
				for (int j = 0; j <= resized.height / sliceHeight; j++){
					BufferedImage crop = getSlice(shot, i, j);
					
					if(crop!=null){
						try{
							ImagePackage imgp = new ImagePackage(i,j,crop);
							sender.send(imgp.getPackage());
							
						}catch(Exception e){
							e.printStackTrace();
						}
					
					}
				}
				
			}
			
			try{
				Thread.sleep(250);
			}catch(Exception e){}
		}
		
		sender.shutdown();
	}
	
	private BufferedImage getSlice(BufferedImage source, int index_width, int index_height){
		/* Fungsi ini diambil dari project casavir (https://code.google.com/p/casavir/)
		 * 
		 * URL Kode:
		 * https://code.google.com/p/casavir/source/browse/trunk/CasavirTestClient/src/com/googlecode/casavir/UDPClient.java?r=20
		 * 
		 * Lisensi: Apache 2.0 (lihat folder license)
		 * 
		 * pengubahan: menghilangkan paramenter slice_width dan slice_height, langsung dari konstanta sliceWidth dan sliceHeight
		 * 
		 */
		
		BufferedImage crop;
		 
		int totalWidth = source.getWidth();
		int totalHeight = source.getHeight();
		 
		int startWidth = index_width * sliceWidth;
		int width = sliceWidth;
		if(startWidth + sliceWidth > totalWidth){
			width = totalWidth - startWidth;
		}
		 
		int startHeight = index_height * sliceHeight;
		int height = sliceHeight;
		if(startHeight + sliceHeight > totalHeight){
			height = totalHeight - startHeight;
		}
		
		if(width > 0 && height > 0){
			crop = source.getSubimage(startWidth, startHeight, width, height);
			return crop;
		}else{
			return null;
		}
	}
}
