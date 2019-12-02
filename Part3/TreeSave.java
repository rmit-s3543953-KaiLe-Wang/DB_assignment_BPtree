import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class TreeSave {
	static int pagesize;
	static int offset;

	public static void main(String[] args) throws IOException {
		if (args.length == 1) {
			pagesize = Integer.parseInt(args[0]);

			// Random Access File to move to and fro of a binary file
			// startTime = System.nanoTime();
			RandomAccessFile file = new RandomAccessFile("heap." + pagesize, "r");
			// note the parameter in constructor is the order/fan-out number.
			BplusTree<String, String> tree = new BplusTree<String, String>(25000);
			offset = 0;
			// ---------------------start actual
			// operation-----------------------------------------
			long startTime = System.nanoTime();
			System.out.println("start reading file and save the tree on the disk");
			int count = 1;
			int file_count = 1;
			while (true) {
				if (count % (5000000 / 6) == 0) {
					writeFile("BPlusTree" + (file_count) + ".txt", tree);
					writeList("RecordList"+(file_count++)+".txt",tree); 
					tree = new BplusTree<String, String>(25000);
				}
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
		        	//System.out.println(da);
		        	tree.insertOrUpdate(da, Integer.toString(offset));
					offset+=26;
					file.seek(offset);
					byte[] d_time = new byte[22];
					file.read(d_time);
					//String departureTime = new String(d_time);
					//System.out.println(new String(d_time));
					offset+=23;
					//String durationSec=
							findData(file);
					//String streetMarker=
							findData(file);
					//String sign=
							findData(file);
					//String area=
							findData(file);
					file.seek(offset);
					byte[] streetId = new byte[4];
					file.read(streetId);
					offset+=5;
					//String streetName=
							findData(file);
					//String between1=
							findData(file);
					//String between2=
							findData(file);
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
					count++;
					//System.out.println("da: "+da);
					//deviceId==Integer.parseInt(query);
					//System.out.println(da.replace(" ",""));
					//if(da.replace(" ","").contains(query))
					/*
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
					}*/
						
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

				catch (EOFException e) {
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					break;
				}

			}
			/*
			 * try (ObjectOutputStream oos = new ObjectOutputStream(new
			 * BufferedOutputStream(new FileOutputStream("BPtree")))) {
			 * oos.writeObject(tree); }
			 */
			writeFile("BPlusTree" + (file_count) + ".txt", tree);
			writeList("RecordList"+(file_count++)+".txt",tree); 
			// -------Time measurement!---------
			long endTime = System.nanoTime();
			double estimatedTime = ((double) (endTime - startTime)) / Math.pow(10, 9);
			System.out.println(" time taken = " + estimatedTime + " sec ");
			System.out.println("All job completed!");

		} else {
			System.err.println("example: TreeSave pagenum(e.g 4096)");
		}

	}

	public static void writeFile(String filename, BplusTree<String, String> tree)
			throws FileNotFoundException, IOException {
		File treeFile = new File(filename);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(treeFile));
		objectOutputStream.writeObject(tree);
		objectOutputStream.close();
	}

	public static void writeList(String filename, BplusTree<String, String> tree) throws IOException {
		BplusNode<String, String> search = tree.getHead();
		BplusNode<String, String> temp;
		File listFile = new File(filename);
		FileWriter f = new FileWriter(listFile);
		// start from the head
		while (search.next!=null) {
			// if it is a leaf node, then get the next value until null given.
			if (search.isLeaf) {
				temp = search;
				for (int i = 0; i < temp.entries.size(); i++) {
					//System.out.println("Key: " + temp.entries.get(i).getKey());
					//System.out.println("Value: " + temp.entries.get(i).getValue());
					f.write(temp.entries.get(i).getValue()+"\n");
				}
				search.traversed=true;
			} else {
				// else, go to the most left hand side children.
				int i = 0;
				// if the node has not yet been traversed
				while (!search.children.get(i).traversed) {
					search = search.children.get(i);
					i++;
				}
			}
			search=search.next;
		}
		f.close();
	}

	public static String readBlock(RandomAccessFile file) throws IOException {
		// to the offset
		file.seek(offset);
		// read 4 bytes for devices and 22 bytes for arrival time = DA_NAME field.
		byte[] DA_NAME = new byte[4 + 22];
		file.read(DA_NAME);
		byte[] d_id = Arrays.copyOfRange(DA_NAME, 0, 4);
		int deviceId = byteArrayToInt(d_id);
		// System.out.println(byteArrayToInt(d_id));
		byte[] a_time = Arrays.copyOfRange(DA_NAME, 4, DA_NAME.length);
		String arrivalTime = new String(a_time);
		String da = deviceId + arrivalTime;
		offset += 26;
		// System.out.println(new String(a_time));
		// see if the DA_NAME fields contain the query statement.

		// if so, continue reading this record.
		// 1. read Departure time
		file.seek(offset);
		byte[] d_time = new byte[22];
		file.read(d_time);
		String departureTime = new String(d_time);
		// System.out.println(new String(d_time));
		offset += 23;
		String durationSec = findData(file);
		String streetMarker = findData(file);
		String sign = findData(file);
		String area = findData(file);
		file.seek(offset);
		byte[] streetId = new byte[4];
		file.read(streetId);
		offset += 5;
		String streetName = findData(file);
		String between1 = findData(file);
		String between2 = findData(file);
		// String side=findData(file,offset);
		// String inViolation=findData(file,offset);

		file.seek(offset);
		byte[] side = new byte[4];
		file.read(side);
		offset += 4;
		file.seek(offset);
		byte[] inViolation = new byte[1];
		file.read(inViolation);
		offset += 2;
		String data = Integer.toString(deviceId) + " " + arrivalTime + " " + departureTime + " " + durationSec + " "
				+ streetMarker + " " + sign + " " + area + " " + byteArrayToInt(streetId) + " " + streetName.toString()
				+ " " + between1.toString() + " " + between2.toString() + " " + byteArrayToInt(side) + " "
				+ new String(inViolation);
		return data;
	}

	public static int byteArrayToInt(byte[] b) {

		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
	}

	public static int byteArrayToShort(byte[] b) {

		return (b[0] & 0xFF) << 24;
	}

	public static String findData(RandomAccessFile file) throws IOException {
		byte[] head = new byte[8];
		file.seek(offset);
		file.read(head);
		String temp = new String("");
		int position = findNext(',', head);
		while (position == -1) {
			temp = temp + new String(head);
			offset += 8;
			file.seek(offset);
			file.read(head);
			int recordEnd = findNext('|', head);
			if (recordEnd != -1) {
				byte[] tp = new byte[8];
				// System.out.println("end of record");
				offset = offset - (5 - recordEnd);
				return temp + new String(head);
			}
			position = findNext(',', head);
		}
		if (position != -1) {
			offset += (position + 1);
			return temp + new String(head).substring(0, position);
		}
		return temp;

	}

	public static int findNext(char target, byte[] source) {
		String s = new String(source);
		// System.out.println("in findNext(), searching string:"+s);
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == target)
				return i;
		}
		return -1;
	}

	public static boolean isExist(String target, byte[] source) {
		String s = new String(source);
		// System.out.println("in findNext(), searching string:"+s);
		return s.contains(target);
	}

	public static int findLast(char target, byte[] source) {
		String s = new String(source);
		// System.out.println("in findNext(), searching string:"+s);
		for (int i = s.length() - 1; i > 0; i--) {
			if (s.charAt(i) == target)
				return i;
		}
		return -1;
	}

}
