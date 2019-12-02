

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class dbquery {
	static String query;
	static int pagesize;
	static int offset;
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		if(args.length == 2){
			query = args[0];
			pagesize = Integer.parseInt(args[1]);
			//Random Access File to move to and fro of a binary file
				//startTime = System.nanoTime();
				RandomAccessFile file = new RandomAccessFile("heap."+pagesize, "r");
				 offset=0;
				//---------------------start actual operation-----------------------------------------
					long startTime = System.nanoTime( );
					System.out.println("start");
					
				while (true){
				
					try{
					// to the offset
					file.seek(offset);
					//read 4 bytes for devices and 22 bytes for arrival time = DA_NAME field.
					byte[] DA_NAME = new byte[4+22];
					file.read(DA_NAME);
					byte[] d_id = Arrays.copyOfRange(DA_NAME,0,4);
					int deviceId= byteArrayToInt(d_id);
					//System.out.println(byteArrayToInt(d_id));
					byte[] a_time = Arrays.copyOfRange(DA_NAME,4, DA_NAME.length);
		        	String arrivalTime = new String(a_time);
		        	String da = deviceId+arrivalTime;
					offset+=26;
					//System.out.println(new String(a_time));
					//see if the DA_NAME fields contain the query statement.
					
						//if so, continue reading this record.
						//1. read Departure time
					file.seek(offset);
					byte[] d_time = new byte[22];
					file.read(d_time);
					String departureTime = new String(d_time);
					//System.out.println(new String(d_time));
					offset+=23;
					String durationSec=findData(file);
					String streetMarker=findData(file);
					String sign=findData(file);
					String area=findData(file);
					file.seek(offset);
					byte[] streetId = new byte[4];
					file.read(streetId);
					offset+=5;
					String streetName=findData(file);
					String between1=findData(file);
					String between2=findData(file);
					//String side=findData(file);
					//String inViolation=findData(file);
					
					file.seek(offset);
					byte[] side = new byte[4];
					file.read(side);
					offset+=4;
					file.seek(offset);
					byte[] inViolation = new byte[1];
					file.read(inViolation);
					offset+=2;
					
					//System.out.println("da: "+da);
					//deviceId==Integer.parseInt(query);
					//System.out.println(da.replace(" ",""));
					if(da.replace(" ","").contains(query))
					{
						System.out.println("deviceid: "+deviceId);
						System.out.println("arrival time: "+arrivalTime);
						System.out.println("departure time: "+departureTime);
						System.out.println("duration: "+durationSec);
						System.out.println("street marker: "+streetMarker);
						System.out.println("sign: "+sign);
						System.out.println("area: "+area);
						System.out.println("street Id: "+byteArrayToInt(streetId));
						System.out.println("street name: "+streetName);
						System.out.println("between street1: "+between1);
						String[] data =between2.split(",");
						//System.out.println("between street2: "+between2);
						System.out.println("between street2: "+data[0]);
						//System.out.println("extra: "+data[1]);
						System.out.println("side of a street: "+byteArrayToInt(side));
						System.out.println("inViolation: "+new String(inViolation));
					}
						else
						{
							//     if not match, find next record by finding next "|", and jump to there.
							//System.out.println("-");
					}	
					// now it is the last of the record. see if the next byte is 0 or not
					// because device id does not start from 0
					// so check it and see if it is the last page.
					byte[] temp = new byte[4];
					//byte[] space = new byte[16];
					file.seek(offset);
					file.read(temp);
				//	file.read(space);
					int zeroO = byteArrayToInt(temp);
					
					if(zeroO==0)
					{
					while(zeroO==0)
					{
						//System.out.println("AT THIS RECORD. ZERO ARE FOUND HERE: "+offset);
						offset+=4;
						file.seek(offset);
						file.read(temp);
						zeroO = byteArrayToInt(temp);
					}
					offset+=1;
					}
					else if (isExist("&&&",temp)) {
						System.out.println("end of the file");
						break;
					}
					
					
					}
				 
					catch(EOFException e)
					{
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						break;
					}
				
				}
				//-------Time measurement!---------
	    		long endTime = System.nanoTime( );
	    		double estimatedTime = ((double)(endTime-startTime))/Math.pow(10,9);
	    		System.out.println( " time taken = " + estimatedTime + " sec " ) ;
	    		System.out.println("All job completed!");
			
		}
		
	}
	public static int byteArrayToInt(byte[] b) {
		
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
	
public static int byteArrayToShort(byte[] b) {
		
	    return  (b[0] & 0xFF) << 24;
	}
	public static String findData(RandomAccessFile file) throws IOException {
		byte[] head = new byte[8];
		file.seek(offset);
		file.read(head);
		String temp=new String("");
		int position=findNext(',',head);
		while(position==-1)
		{
			temp = temp+new String(head);
			offset+=8;
			file.seek(offset);
			file.read(head);
			int recordEnd =findNext('|',head);
			if(recordEnd!=-1)
			{
				byte[] tp = new byte[8];
				//System.out.println("end of record");
				offset=offset-(5-recordEnd);	
				return temp+new String(head);
			}
			position=findNext(',',head);
		}
		if(position!=-1)
		{
			offset +=(position+1);
			return temp+new String(head).substring(0, position);
		}
		return temp;
		
	}
	
	public static int findNext(char target, byte[] source) {
		String s= new String(source);
		//System.out.println("in findNext(), searching string:"+s);
		for (int i=0; i <s.length();i++)
		{
			if(s.charAt(i)==target)
				return i;
		}
		return -1;
	}
	public static boolean isExist(String target, byte[] source) {
		String s= new String(source);
		//System.out.println("in findNext(), searching string:"+s);
		return s.contains(target);
	}
	public static int findLast(char target, byte[] source) {
		String s= new String(source);
		//System.out.println("in findNext(), searching string:"+s);
		for (int i=s.length()-1; i >0;i--)
		{
			if(s.charAt(i)==target)
				return i;
		}
		return -1;
	}

}
