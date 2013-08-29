package faiz.rdp.datapackage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


public class ImagePackage {
	public int index_x, index_y, width, height;
	public byte[] imagedata;
	
	public static final float factor = 0.7f;
	public static final int SLICE_WIDTH = 256, SLICE_HEIGHT = 256;
	
	public ImagePackage(int x, int y, BufferedImage img) throws IOException{
		width = img.getWidth();
		height = img.getHeight();
		
		this.index_x = x;
		this.index_y = y;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", baos);
		baos.flush();
		imagedata = baos.toByteArray();
		
		baos.close();
	}
	
	public ImagePackage(byte[] raw) throws IOException{
		if (raw.length <= 8) throw new IOException();
		
		index_x = PackageHelper.getInt(raw, 0);
		index_y = PackageHelper.getInt(raw, 2);
		width = PackageHelper.getInt(raw, 4);
		height = PackageHelper.getInt(raw, 6);
		
		imagedata = new byte[raw.length - 8];
		PackageHelper.getByte(raw, imagedata, 8);
	}
	
	public byte[] getPackage(){
		byte[] data = new byte[8 + imagedata.length];
		
		PackageHelper.putInt(index_x, data, 0);
		PackageHelper.putInt(index_y, data, 2);
		PackageHelper.putInt(width, data, 4);
		PackageHelper.putInt(height, data, 6);
		PackageHelper.putByte(imagedata, data, 8);

		
		return data;
	}
	
	public BufferedImage buildImage(){
		BufferedImage img = null;
		
		try{
			img = ImageIO.read(new ByteArrayInputStream(imagedata));
			
			return img;
		}catch(Exception e){
			return null;
		}
	}
	
	
}
