package faiz.rdp.datapackage;

public class PackageHelper {
	// write read byte
	
		public static void putInt(int src, byte[] dest, int start){
			putByte(intToByte(src), dest, start);
		}
		
		public static void putByte(byte[] src, byte[] dest, int start){
			//if (src.length + start > dest.length) return;
			
			for (int i = 0; i < src.length; i++){
				dest[i + start] = src[i];
			}
			
		}
		
		public static void getByte(byte[] src, byte[] dest, int start){
			//if (src.length <= start) return;
			
			for (int i = 0; i < dest.length; i++){
				dest[i] = src[i + start];
			}
		}
		
		public static int getInt(byte[] src, int start){
			byte[] data = new byte[2];
			getByte(src, data, start);
			//System.out.println((data[0] & 0xFF) + " " + (data[1] & 0xFF));
			return byteToInt(data);
		}
		
		//int - byte conversion, big endian
		
		public static byte[] intToByte(int n){
			byte[] data = new byte[2];

			data[0] = (byte) ((n >> 8) & 0xFF);
			data[1] = (byte) (n & 0xFF);
			
			return data;
		}
		
		public static int byteToInt(byte[] data){
			return ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
		}
}
