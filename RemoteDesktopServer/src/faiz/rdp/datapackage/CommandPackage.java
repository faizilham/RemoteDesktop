package faiz.rdp.datapackage;

public class CommandPackage {
	public static final int PRE_CONNECT = 0; // code ip_b(4) w h. 6 component, ip 4 + w/h 2
	public static final int PRE_DISCONNECT = 1; //code. no component
	public static final int PRE_CURSOR = 2; //code x y. 2 component
	public static final int PRE_KEY = 3; //code state_b key_code. 2 component
	public static final int PRE_CLICK = 4; //code button_b state_b x y. 4 component
	public static final int PRE_ALIVE = 5; //code
	
	private static int[][] FORMAT = {{4,2}, {0,0}, {0,2}, {1,1}, {2,2}, {0,0}};
	
	/*
	 * PACKAGE FORMAT
	 * code bytes ints
	 * 
	 */
	
	public int code;
	public byte[] bytecomponent = null;
	public int[] intcomponent = null;
	
	private CommandPackage(int code){
		this.code = code;
		int nbyte = FORMAT[code][0];
		int nint = FORMAT[code][1];
		
		if (nbyte > 0) bytecomponent = new byte[nbyte];
		if (nint > 0) intcomponent = new int[nint];
	}
	
	public CommandPackage (byte[] rawdata){
		code = rawdata[0];
		int nbyte = FORMAT[code][0];
		int nint = FORMAT[code][1];
		
		if (nbyte > 0) {
			bytecomponent = new byte[nbyte];
			
			PackageHelper.getByte(rawdata, bytecomponent, 1);
		}
		
		if (nint > 0) {
			intcomponent = new int[nint];
			
			for (int i = 0; i < nint; i++){
				intcomponent[i] = PackageHelper.getInt(rawdata, 1+nbyte+i*2);
			}
		}
		
	}
	
	public byte[] getPackage(){
		
		int nbyte = bytecomponent == null ? 0: bytecomponent.length;
		int nint = intcomponent == null ? 0: intcomponent.length;

		
		byte[] data = new byte[1 + nbyte + nint * 2];
		
		data[0] = (byte) code;
		
		if (nbyte > 0)
			PackageHelper.putByte(bytecomponent, data, 1);
		
		for (int i = 0; i < nint; i++){
			PackageHelper.putInt(intcomponent[i], data, 1 + nbyte + i*2);
		}
				
		return data;
	}
	
	public String toString(){
		StringBuilder b = new StringBuilder();
		int nbyte = bytecomponent == null ? 0: bytecomponent.length;
		int nint = intcomponent == null ? 0: intcomponent.length;
		
		b.append(code);
		b.append(";");
		for (int i = 0; i < nbyte; i++) b.append(((int) bytecomponent[i] & 0xFF) + " ");
		b.append(";");
		for (int i = 0; i < nint; i++) b.append(intcomponent[i] + " ");
		
		return b.toString();
	}
	
	public static CommandPackage buildConnectionPackage(String ip, int screenWidth, int screenHeight){
		CommandPackage pkg = new CommandPackage(PRE_CONNECT);
		
		
		
		String[] ipsplit = ip.split("\\.");
		
		for (int i = 0; i < 4; i++){
			pkg.bytecomponent[i] = (byte) Integer.parseInt(ipsplit[i]);
		}
		
		pkg.intcomponent[0] = screenWidth;
		pkg.intcomponent[1] = screenHeight;
		
		return pkg;
	}
	
	public static CommandPackage buildCursorPackage(int x, int y){
		CommandPackage pkg = new CommandPackage(PRE_CURSOR);
		
		pkg.code = PRE_CURSOR;
		
		pkg.intcomponent[0] = x;
		pkg.intcomponent[1] = y;
		
		return pkg;
	}
	
	public static CommandPackage buildClickPackage(int button, int state, int x, int y){
		// button: 1 left, 2 middle, 3 right
		// state: 0 press, 1 release
		
		CommandPackage pkg = new CommandPackage(PRE_CLICK);

		pkg.bytecomponent[0] = (byte) button;
		pkg.bytecomponent[1] = (byte) state;
		pkg.intcomponent[0] = x;
		pkg.intcomponent[1] = y;
		return pkg;
	}
	
	public static CommandPackage buildKeyPackage(int key, int state){
		// state: 0 press, 1 release
		CommandPackage pkg = new CommandPackage(PRE_KEY);
		
		pkg.bytecomponent[0] = (byte) state;
		pkg.intcomponent[0] = key;
		
		return pkg;
	}
	
	public static CommandPackage buildDisconnectPackage(){
		CommandPackage pkg = new CommandPackage(PRE_DISCONNECT);
		return pkg;
	}
	
	public static CommandPackage buildKeepAlivePackage(){
		CommandPackage pkg = new CommandPackage(PRE_ALIVE);
		return pkg;
	}
}
