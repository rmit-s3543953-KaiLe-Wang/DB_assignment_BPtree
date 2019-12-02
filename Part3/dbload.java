import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

public class dbload {

	static String filepath; //the 'datafile' argument
	static int pagesize; //page size argument
	int deviceID_size = 4;
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
	if(args.length ==3)
	{
		//System.out.println("3 arguments!");
		if (args[0].equals("-p"))
		{
			//System.out.println("args[0] passed");
			//System.out.println("1/2P MTR M-SAT 7:30-19:30".getBytes() +","+"length ="+"1/2P MTR M-SAT 7:30-19:30".length());
			//System.out.println("08/11/2017 02:19:50 PM".getBytes() +","+"length ="+"08/11/2017 02:19:50 PM".length());
			filepath= args[2];
			pagesize= Integer.parseInt(args[1]);
	        String line = "";
	        String cvsSplitBy = "\\s*,";
	      //---------------------start actual operation-----------------------------------------
			long startTime = System.nanoTime( );
	        int recordCount=0;//how many records in total
	        int pageCount=0; // how many pages have been written
	        int sizeCount=0; // how much data has been written in bytes
	        String recordDelim = "|";//to identify each record.
	        String varDelim =","; //to identify a variable length data. e.g. 1345,swanston st,1234. will try to read comma first.
	        byte[] recordDelimByte = recordDelim.getBytes();
	        byte[] varDelimByte = varDelim.getBytes();
	        //System.out.println("length for |: "+recordDelimByte.length);
	        //-----list of records size. even though some of the sizes are not fixed like int type, it is necessary to give out an approx of the maximum record size.
	        // the details for these number please see below variables declaration.
	        int recordSize=4+22+22+22+10+8+26+17+30+30+30+1+1;
	        //Before read, the program need to see if the record size is less than the page size or not.
	        //For safety, an equal size is not allowed.
	        if (pagesize<=recordSize) {
	        	System.err.println("The page size provided is too small! please use a greater size");
	        	System.exit(0);
	        }
	        //Start reading csv file and assign all variables.
	        DataOutputStream outStream = new DataOutputStream(new FileOutputStream("heap." + pagesize));
	        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
	        	String[] data = line.split(cvsSplitBy);//ignore titles
	        	line = br.readLine();//ignore titles
	        	while ((line = br.readLine()) != null) {
	        	int Rsize =0;//size for each record
	        	data = line.split(cvsSplitBy);
	        	int deviceId = Integer.parseInt(data[0]);// 4 bytes.
	        	String arrival_time = data[1];// same as departure time, 22.
	        	String departure_time = data[2];// length of 22 will be ok
	        	String duration_second = data[3];// 10 bytes. in general it will be less than that, however there are some very large time there, and sign is negative. not sure why
	        	String stMarker = data[4];// ~8 bytes?
	        	String sign = data[5]; //26
	        	String area = data[6]; //17
	        	int streetId = Integer.parseInt(data[7]); // 4 bytes integer
	        	String stName = data[8];// 30 bytes
	        	String betweenSt1 = data[9];// 30 bytes
	        	String betweenSt2 = data[10];//30 bytes.
	        	short side = Short.parseShort(data[11]);//very short number, 2 bytes
	        	String inViolation = data[12].substring(0,1);//True(T) or False(F), 2 bytes
	        	//---------convert all data into byte arrays-----------
	        	//ByteBuffer.allocate(4).putInt(deviceId).array()
	        	byte[] dIDB =intToByteArray(deviceId);
	        	byte[] a_timeB = arrival_time.getBytes();
	        	byte[] DA_NAME = new byte [dIDB.length+a_timeB.length];
	        	//then put dIDB and a_timeB together forms a DA_NAME field.
	        	System.arraycopy(dIDB, 0, DA_NAME, 0, dIDB.length);
	        	System.arraycopy(a_timeB, 0, DA_NAME, dIDB.length, a_timeB.length);
	        	//System.out.println(byteArrayToInt(dIDB));
	        	//byte[] a_time = Arrays.copyOfRange(DA_NAME,4, DA_NAME.length);
	        	//System.out.println(new String(a_time));
	        	byte[] d_timeB = departure_time.getBytes();
	        	byte[] d_secB = duration_second.getBytes();
	        	byte[] stMarkerB = stMarker.getBytes();
	        	byte[] signB = sign.getBytes();
	        	byte[] areaB = area.getBytes();
	        	byte[] stIDB = intToByteArray(streetId);
	        	byte[] stNameB = stName.getBytes();
	        	byte[] btNameB1 = betweenSt1.getBytes();
	        	byte[] btNameB2 =betweenSt2.getBytes();
	        	byte[] sideB =  intToByteArray(side);
	        	byte[] inVioB = inViolation.getBytes();
	        	//System.out.println(new String(inVioB)+" "+inVioB.length);
	        	//output the file.
	        	
	        	outStream.write(DA_NAME);
	        	outStream.write(d_timeB);
	        	outStream.write(varDelimByte);
	        	outStream.write(d_secB);
	        	outStream.write(varDelimByte);
	        	outStream.write(stMarkerB);
	        	outStream.write(varDelimByte);
	        	outStream.write(signB);
	        	outStream.write(varDelimByte);
	        	outStream.write(areaB);
	        	outStream.write(varDelimByte);
	        	outStream.write(stIDB);
	        	outStream.write(varDelimByte);
	        	outStream.write(stNameB);
	        	outStream.write(varDelimByte);
	        	outStream.write(btNameB1);
	        	outStream.write(varDelimByte);
	        	outStream.write(btNameB2);
	        	outStream.write(varDelimByte);
	        	outStream.write(sideB);
	        	outStream.write(inVioB);
	        	outStream.write(recordDelimByte);
	        	
	        	sizeCount = sizeCount+recordDelimByte.length+dIDB.length+a_timeB.length
                        +d_timeB.length+d_secB.length+stMarkerB.length+signB.length
                        +areaB.length+stIDB.length+stNameB.length+btNameB1.length+btNameB2.length
                        +sideB.length+inVioB.length;
	        	recordCount++;
	        	//System.out.println("current record:"+recordCount);
	        	//if there is no much space for a record, fill zeros onto it until the page size is achieved.
	        	while(pagesize<recordSize+sizeCount) {
	        		//System.out.println(sizeCount);
	        		outStream.write(0);
	        		sizeCount++;
	        		if(sizeCount==pagesize) {
	        			//System.out.println("current size:"+sizeCount);
	        			//System.out.println("this page has: "+recordCount+" records");
	        			sizeCount=0;
	        			recordCount=0;
	        			pageCount++;
	        			break;
	        		}
	        	}
	        	}
	        }
	        finally{
	        	pageCount++;
	        	//System.out.println("the final page has: "+recordCount+" records");
	        	outStream.write("&&&".getBytes());//indicates the end of the file.
	        	outStream.close();
	        	//System.out.println("job done");
	        	System.out.println(pageCount+" pages written");
	        	//-------Time measurement!---------
	    		long endTime = System.nanoTime( );
	    		double estimatedTime = ((double)(endTime-startTime))/Math.pow(10,9);
	    		System.out.println( " time taken = " + estimatedTime + " sec " ) ;
	    		System.out.println("All job completed!");
	        }
		}
		
		else
			System.err.println("incorrect args[0]");
		
	}
	else
	{
		System.err.println("needs 3 arguments!");
	}
	
	}
	public static byte[] intToByteArray(int a) {
	    return new byte[] {
	        (byte) ((a >> 24) & 0xFF),
	        (byte) ((a >> 16) & 0xFF),   
	        (byte) ((a >> 8) & 0xFF),   
	        (byte) (a & 0xFF)
	    };
	}
	public static int byteArrayToInt(byte[] b) {
		
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}

}

