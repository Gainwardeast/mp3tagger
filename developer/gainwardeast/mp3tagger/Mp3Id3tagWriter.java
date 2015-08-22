package developer.gainwardeast.mp3tagger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class Mp3Id3tagWriter {

	private Mp3Id3tagObjectId3v1 id3v1Object;
	private Mp3Id3tagObjectId3v2 id3v2Object;
	private String pathToFile;
	private int id3v1_tagSize = 128;
	private final String ID3V1_MP3TAG_HEADER = "TAG";
	
	public Mp3Id3tagWriter(Mp3Id3tagObjectId3v1 id3v1Object,Mp3Id3tagObjectId3v2 id3v2Object, String pathToFile)
	{
		this.id3v1Object = id3v1Object;
		this.id3v2Object = id3v2Object;
		this.pathToFile = pathToFile;
	}
	
	public void submitNewTagsToFile() throws IOException
	{
		if(id3v1Object != null) 
		{
	    	RandomAccessFile rafObject = new RandomAccessFile(new File(pathToFile), "rw");
	    	// prepare id3v1 bytes[] for writing
	    	byte[] id3v1tagData = new byte[id3v1_tagSize];
	        byte[] tag = new byte[3]; 
	        tag = ID3V1_MP3TAG_HEADER.getBytes("utf8");
	        byte[] tagTitle = new byte[30];
	        tagTitle = Arrays.copyOfRange(id3v1Object.get_title_id3v1().getBytes("cp1251"), 0, 30);
	        byte[] tagArtist = new byte[30];
	        tagArtist = Arrays.copyOfRange(id3v1Object.get_author_id3v1().getBytes("cp1251"), 0, 30);
	        byte[] tagAlbum = new byte[30];
	        tagAlbum = Arrays.copyOfRange(id3v1Object.get_album_id3v1().getBytes("cp1251"), 0, 30);
	        byte[] tagYear = new byte[4];
	        tagYear = Arrays.copyOfRange(id3v1Object.get_year_id3v1().getBytes("cp1251"), 0, 4);
	        byte[] tagComment = new byte[30];
	        tagComment = Arrays.copyOfRange(id3v1Object.get_comment_id3v1().getBytes("cp1251"), 0, 30);
	        byte[] tagGenre = new byte[1];
	        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	        byteStream.write(tag);
	        byteStream.write(tagTitle);
	        byteStream.write(tagArtist);
	        byteStream.write(tagAlbum);
	        byteStream.write(tagYear);
	        byteStream.write(tagComment);
	        byteStream.write(tagGenre);
	        id3v1tagData = byteStream.toByteArray();
	        System.out.println("id3v1tagData.length() "+id3v1tagData.length);
	        System.out.println("rafObject.length() "+rafObject.length());
		}
		
		if(id3v2Object != null) 
		{	
			deleteOldId3v2Tags(pathToFile,id3v2Object.getTagsSize());
			
			RandomAccessFile rafObject = new RandomAccessFile(new File(pathToFile), "rw");
	    	// prepare id3v2 bytes[] for writing  
			// ================================= header
			byte[] headerData = id3v2Object.getHeaderBytes();			
			// ================================= write frames bodies
			ByteArrayOutputStream textFramesTagsDataByteStream = new ByteArrayOutputStream();
			if(!id3v2Object.get_title_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("TIT2",id3v2Object.get_title_id3v2().getBytes("utf8")));
			if(!id3v2Object.get_author_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("TPE1",id3v2Object.get_author_id3v2().getBytes("utf8")));
			if(!id3v2Object.get_tracknumber_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("TRCK",id3v2Object.get_tracknumber_id3v2().getBytes("utf8")));
			if(!id3v2Object.get_publisher_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("TPUB",id3v2Object.get_publisher_id3v2().getBytes("utf8")));
			if(!id3v2Object.get_original_artist_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("TOPE",id3v2Object.get_original_artist_id3v2().getBytes("utf8")));
			if(!id3v2Object.get_copyright_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("WCOP",id3v2Object.get_copyright_id3v2().getBytes("utf8")));
			if(!id3v2Object.get_attached_url_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("WXXX",id3v2Object.get_attached_url_id3v2().getBytes("utf8")));
			if(!id3v2Object.get_encoder_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("TENC",id3v2Object.get_encoder_id3v2().getBytes("utf8")));
			if(!id3v2Object.get_album_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("TALB",id3v2Object.get_album_id3v2().getBytes("utf8")));
			if(!id3v2Object.get_composer_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("TCOM",id3v2Object.get_composer_id3v2().getBytes("utf8")));
			if(!id3v2Object.get_year_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("TYER",id3v2Object.get_year_id3v2().getBytes("utf8")));
			if(!id3v2Object.get_comment_id3v2().equals(""))textFramesTagsDataByteStream.write(getTogetherTextFrame("COMM",id3v2Object.get_comment_id3v2().getBytes("utf8")));
			
			ByteArrayOutputStream tagsDataByteStream = new ByteArrayOutputStream();
			byte[] lengthDataArray = intToByteArray(textFramesTagsDataByteStream.size());
			
			headerData[4] = 4; // tags version - the 4th byte/ we have tags version 2.4
			headerData[6] = (lengthDataArray[0] == 0) ? 0 : lengthDataArray[0];
			headerData[7] = (lengthDataArray[1] == 0) ? 0 : lengthDataArray[1];
			headerData[8] = (lengthDataArray[2] == 0) ? 0 : lengthDataArray[2];
			headerData[9] = (lengthDataArray[3] == 0) ? 0 : lengthDataArray[3];
			tagsDataByteStream.write(headerData);
			// ================= write  the whole bunch of bytes (header + frames)
			tagsDataByteStream.write(textFramesTagsDataByteStream.toByteArray()); 
			// ================= getTogetherPicFrame
			if(id3v2Object.get_attachedpicture_id3v2() != null)
			{
				tagsDataByteStream.write(getTogetherPicFrame(id3v2Object.get_attachedpicture_id3v2()));
			}
			writeNewId3v2Tags(pathToFile,tagsDataByteStream.toByteArray());
		}
	}
	
	private byte[] getTogetherTextFrame(String framename,byte[] frameTextData) {
		ByteArrayOutputStream frameDataByteStream = new ByteArrayOutputStream();
		// id3 v2 tagframe = framenameData + framesizeData + char code + frame text // utf 8 is charcode 3
		try {
			byte[] frameNameData = new byte[4];
			frameNameData = framename.getBytes("utf8");
			int framesize = frameTextData.length + 1;
			System.out.println("for framename "+new String(frameNameData,"utf8")+" framename length "+framename.length()+" framesize is "+framesize);
			byte[] frameSizeData = intToByteArray(framesize);
			frameDataByteStream.write(frameNameData);
			frameDataByteStream.write(frameSizeData);
			// write empty bytes
			frameDataByteStream.write(0); // byte 8
			frameDataByteStream.write(0); // byte 9
			byte charcode = (byte)3; // charcode byte 10
			frameDataByteStream.write(charcode);
			frameDataByteStream.write(frameTextData);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return frameDataByteStream.toByteArray();
	}
	
	private byte[] getTogetherPicFrame(byte[] imageData)
	{
		ByteArrayOutputStream frameHeaderDataByteStream = new ByteArrayOutputStream();
		ByteArrayOutputStream frameDataByteStream = new ByteArrayOutputStream();
		int encodingType = 3;
		String mimeType = "image/jpeg"; // set one 0x00 after
		int picType = 6;
		String description = "Album cover";  // set one 0x00 after
		
		frameDataByteStream.write(encodingType);
		try {
			frameDataByteStream.write(mimeType.getBytes("utf8"));
			frameDataByteStream.write(0);
			frameDataByteStream.write(picType);
			frameDataByteStream.write(description.getBytes("utf8"));
			frameDataByteStream.write(0);
			frameDataByteStream.write(imageData);
			int frameSize = frameDataByteStream.toByteArray().length;
			frameHeaderDataByteStream.write(new String("APIC").getBytes("utf8"));
			frameHeaderDataByteStream.write(intToByteArray(frameSize));
			frameHeaderDataByteStream.write(0);
			frameHeaderDataByteStream.write(0);
			frameHeaderDataByteStream.write(frameDataByteStream.toByteArray());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return frameDataByteStream.toByteArray();
	}
	
	private void deleteOldId3v2Tags(String pathToFile,int tagsSize) throws FileNotFoundException
	{
		try {
			new File(pathToFile+"temp").createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileChannel in = new FileInputStream(new File(pathToFile)).getChannel();
		FileChannel out = new FileOutputStream(new File(pathToFile+"temp")).getChannel();
		try {
			in.transferTo(tagsSize, in.size(), out);
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new File(pathToFile).delete();
		new File(pathToFile+"temp").renameTo(new File(pathToFile));
	}
	
	private void writeNewId3v2Tags(String pathToFile,byte[] tagsData) throws FileNotFoundException
	{
		try {
			new File(pathToFile+"temp").createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileChannel in = new RandomAccessFile(pathToFile, "r").getChannel();
		FileChannel out = new RandomAccessFile(pathToFile+"temp", "rw").getChannel();
		try {
			out.write(ByteBuffer.wrap(tagsData));
			in.transferTo(0, in.size(), out);
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new File(pathToFile).delete();
		new File(pathToFile+"temp").renameTo(new File(pathToFile));
	}
		
	private byte[] intToByteArray(int value)
	{
		byte[] byteArray = new byte[]{(byte)(value >> 24),(byte)(value >> 16),(byte)(value >> 8),(byte)value};
		return byteArray;
	}
	
	private int byteArrayToInteger(byte[] array)
	{
		int valueFromArray = 
		(array[3] & 0xFF ) | ((array[2] & 0xFF) << 8 ) | ((array[1] & 0xFF) << 16 )  | ((array[0] & 0xFF) << 24 );
		return valueFromArray;
	}	
	
	private void writetolog(String str)
	{
		System.out.println(str);
	}
	
}