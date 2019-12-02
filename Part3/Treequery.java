import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Scanner;

public class Treequery {
	static int pagesize;
	static long offset;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		long startTime = System.nanoTime();
		long endTime;
		double estimatedTime;
		if (args.length == 5) {
			pagesize = Integer.parseInt(args[0]);// e.g 4096
			String query_part1 = args[1];// e.g 4096 >1971108/11/2017 02:19:50 PM
			String query_part2 = args[2];// e.g 02:19:50
			String query_part3 = args[3];// e.g PM
			// Random Access File to move to and for of a binary file
			// startTime = System.nanoTime();
			RandomAccessFile file = new RandomAccessFile("heap." + pagesize, "r");
			BplusTree<String, String> tree = null;
			String filename = args[4];
			System.out.println("Start to read tree file and load it into memory");
			startTime = System.nanoTime();
			try {
				FileInputStream fis = new FileInputStream(filename);
				ObjectInputStream ois = new ObjectInputStream(fis);
				tree = (BplusTree<String, String>) ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
			endTime = System.nanoTime();
			estimatedTime = ((double) (endTime - startTime)) / Math.pow(10, 9);
			System.out.println(" time taken = " + estimatedTime + " sec ");
			offset = 0;
			// ------search from tree and get record
			startTime = System.nanoTime();
			System.out.println("start query");
			String query = query_part1 + " " + query_part2 + " " + query_part3;
			// ">1971108/11/2017 02:19:50 PM";

			int mode = query.substring(0, 1).equals("L") ? 1 : (query.substring(0, 1).equals("M") ? 2 : 0);
			System.out.println("mode=" + mode);
			// range search
			if (mode != 0) {
				String sign = "";
				if (mode == 1)
					sign = "L";
				else if (mode == 2)
					sign = "M";
				query = query.replace(sign, "");

				// System.out.println("raw:"+tree.getLessOrMore(query,mode));
				String[] results = tree.getLessOrMore(query, null, mode).split(",");
				System.out.println("single range search:");

				if (results != null) {
					if (results.length != 1) {
						for (String result : results) {
							if (result.equals(""))
								continue;
							offset = Long.parseLong(result);
							// System.out.println("pointer: " + offset);
							// System.out.println("data gathered:" + readBlock(file));
						}
					} else {
						offset = Long.parseLong(tree.get(query));
						System.out.println("pointer: " + tree.get(query));
						System.out.println("data gathered:" + readBlock(file));
					}
					System.out.println(results.length + " results found!");
				}
				if (results == null)
					System.out.println("0 result found.");
			}
			// equality search
			else {
				if (tree.get(query) != null) {
					System.out.println("equality search:");
					String[] results = tree.get(query).split(",");
					if (results.length > 1) {
						for (String result : results) {
							offset = Long.parseLong(tree.get(result));
							System.out.println("pointer: " + offset);
							System.out.println("data gathered:" + readBlock(file));
						}
					} else {
						offset = Long.parseLong(tree.get(query));
						System.out.println("pointer: " + offset);
						System.out.println("data gathered:" + readBlock(file));
					}
				} else
					System.out.println("0 result found in file: " + filename);
			}
		}
		// range search

		else if (args.length == 9) {
			pagesize = Integer.parseInt(args[0]);// e.g 4096
			String query_part1 = args[1];// e.g 4096 >1971108/11/2017 02:19:50 PM
			String query_part2 = args[2];// e.g 02:19:50
			String query_part3 = args[3];// e.g PM
			String query2_part1 = args[4];// second query for a range search
			String query2_part2 = args[5];
			String query2_part3 = args[6];
			String filename = args[7];// file name for tree file
			String listName = args[8];// file name for the list file.

			// Random Access File to move to and for of a binary file
			// startTime = System.nanoTime();
			RandomAccessFile file = new RandomAccessFile("heap." + pagesize, "r");
			BplusTree<String, String> tree = null;
			System.out.println("Start to read tree file and load it into memory");
			startTime = System.nanoTime();
			try {
				FileInputStream fis = new FileInputStream(filename);
				ObjectInputStream ois = new ObjectInputStream(fis);
				tree = (BplusTree<String, String>) ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
			endTime = System.nanoTime();
			estimatedTime = ((double) (endTime - startTime)) / Math.pow(10, 9);
			System.out.println(" time taken = " + estimatedTime + " sec ");

			offset = 1;
			// ------search from tree and get record
			startTime = System.nanoTime();
			System.out.println("start range query");
			String query = query_part1 + " " + query_part2 + " " + query_part3;
			// ">1971108/11/2017 02:19:50 PM";
			String query2 = query2_part1 + " " + query2_part2 + " " + query2_part3;
			// compare query1 and 2, to see which one is upper bound, and which one is lower
			// bound.
			// if query 1 is less than query 2
			String upperbound = new String();
			String lowerbound = new String();
			if (query.compareTo(query2) < 0) {
				upperbound = query2;
				lowerbound = query;
			} else {
				upperbound = query;
				lowerbound = query2;
			}
			// range search
			System.out.println("Range search:");
			try {
				BufferedReader f = new BufferedReader(new FileReader(listName));
				findRange(tree, lowerbound, upperbound, f, file);
			} catch (FileNotFoundException e) {
				System.err.println("File not found!");
			}
		}
		// -------Time measurement!---------
		endTime = System.nanoTime();
		estimatedTime = ((double) (endTime - startTime)) / Math.pow(10, 9);
		System.out.println(" time taken = " + estimatedTime + " sec ");
		System.out.println("All job completed!");

	}

	public static void findRange(BplusTree<String, String> tree, String lowerBound, String upperBound,
			BufferedReader file, RandomAccessFile heap) throws IOException {
		int counter = 0;
		// Scanner sc = new Scanner(file);
		// try
		{
			String lowerBoundLocation = tree.get(lowerBound);
			String upperBoundLocation = tree.get(upperBound);
			long lowerLocation = Long.parseLong(lowerBoundLocation);
			long upperLocation = Long.parseLong(upperBoundLocation);
			System.out.println("lower pointer:" + lowerLocation);
			System.out.println("upper pointer:" + upperLocation);
			boolean start = false;
			String line;
			while ((line = file.readLine()) != null && (lowerLocation != upperLocation)) {
				String temp = line;
				String[] temp_result = temp.split(",");
				if (temp_result.length != 1) {
					for (int i = 0; i < temp_result.length; i++) {
						if (Long.parseLong(temp_result[i]) == lowerLocation) {
							System.out.println("lower bound found, start searching");
							start = true;
						}
					}
				}
				// System.out.println(Long.parseLong(temp));
				else {
				if (Long.parseLong(temp) == lowerLocation) {
					System.out.println("lower bound found, start searching");
					start = true;
				}
				}
				if (start) {
					if (temp_result.length != 1) {
						for (int i = 0; i < temp_result.length; i++) {
							lowerLocation = Long.parseLong(temp_result[i]);
							// System.out.println("pointer: " + sc.nextLine());
							offset = lowerLocation;
							// System.out.println("data gathered: " + readBlock(heap));
							counter++;
							}
						}
					else {
						lowerLocation = Long.parseLong(line);
						// System.out.println("pointer: " + sc.nextLine());
						offset = lowerLocation;
						// System.out.println("data gathered: " + readBlock(heap));
						counter++;
					}
				}
			}
			System.out.println("outer loop");
			System.out.println("lower pointer:" + lowerLocation);
			System.out.println("upper pointer:" + upperLocation);
		}

		System.out.println(counter + " results found!");
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
				+ " " + between1.toString() + " " + between2.toString() + " ";
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
