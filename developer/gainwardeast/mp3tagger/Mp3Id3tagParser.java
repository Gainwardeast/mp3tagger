package developer.gainwardeast.mp3tagger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mozilla.universalchardet.UniversalDetector;

public class Mp3Id3tagParser {

	private int id3v1_tagSize = 128;
	private File target_file; 
	private final String ID3V1_MP3TAG_HEADER = "TAG";
	private boolean debugMode;
	
	public Mp3Id3tagParser(File file,boolean setDebugMode)
	{
		this.target_file = file;
		this.debugMode = setDebugMode;
	}
//=========================================================================id 3 v 1
    public Mp3Id3tagObjectId3v1 readTagId3v1() throws IOException 
    {
    	Mp3Id3tagObjectId3v1 target_object = new Mp3Id3tagObjectId3v1();
    	RandomAccessFile raf = new RandomAccessFile(target_file, "r");
        byte[] tagData = new byte[id3v1_tagSize];
        raf.seek(raf.length() - id3v1_tagSize);
        raf.read(tagData);
        ByteBuffer bBuf = ByteBuffer.allocate(id3v1_tagSize);
        bBuf.put(tagData);
        bBuf.rewind();
        byte[] tag = new byte[3];
        byte[] tagTitle = new byte[30];
        byte[] tagArtist = new byte[30];
        byte[] tagAlbum = new byte[30];
        byte[] tagYear = new byte[4];
        byte[] tagComment = new byte[30];
        byte[] tagGenre = new byte[1];
        bBuf.get(tag)
	        .get(tagTitle)
	        .get(tagArtist)
	        .get(tagAlbum)
	        .get(tagYear)
	        .get(tagComment)
	        .get(tagGenre);
        if(!new String(tag).equals(ID3V1_MP3TAG_HEADER))
        {
        		if(debugMode)System.out.println("ByteBuffer does not contain ID3 v1 tag data");
        }
        else 
        {       	
        	String title = new String(tagTitle,getId3v1TextEncoding(tagTitle));
        	String author = new String(tagArtist,getId3v1TextEncoding(tagArtist));
        	String album = new String(tagAlbum,getId3v1TextEncoding(tagAlbum));
        	String year = new String(tagYear,getId3v1TextEncoding(tagYear));
        	String comment = new String(tagComment,getId3v1TextEncoding(tagComment));
        	int Genre = tagGenre[0];
        	target_object = new Mp3Id3tagObjectId3v1();
        	target_object.set_title_id3v1(title);
        	target_object.set_author_id3v1(author);
        	target_object.set_album_id3v1(album);
        	target_object.set_year_id3v1(year);
        	target_object.set_comment_id3v1(comment);
        }
        
        return target_object;
    }    
//=========================================================================id 3 v 2    
    
	public Mp3Id3tagObjectId3v2 readTagId3v2(boolean returnAttachedPicture) throws IOException 
	{
		int id3v2_header_size = 10;
		byte[] header_tag_data = new byte[id3v2_header_size];

		RandomAccessFile raf = new RandomAccessFile(target_file, "r");
		raf.seek(0);
		raf.read(header_tag_data, 0, id3v2_header_size);
		ByteBuffer bBuf = ByteBuffer.allocate(id3v2_header_size);
		bBuf.put(header_tag_data);
		bBuf.rewind();
		//==========================================read id3 v2 header
		byte[] tag = new byte[3];
		byte[] tag_version = new byte[1];
		byte[] tag_subversion = new byte[1];
		byte[] tag_flags = new byte[1];
		byte[] tag_alltags_length = new byte[4];

		bBuf.get(tag)
			.get(tag_version)
			.get(tag_subversion)
			.get(tag_flags)
			.get(tag_alltags_length);
		
		if(!new String(tag).equals("ID3")) return null;
		
		final int id3v2_tag_version = tag_version[0];
		if(debugMode)System.out.println("id3v2_tag_version "+id3v2_tag_version);
		//===================================================check ID3v2 flags
		boolean has_extended_hdr = (tag_flags[0] & 0x40) != 0 ? true : false; 
		if (has_extended_hdr) {
			if(debugMode)System.out.println("has_extended_hdr yes");
		} else {
			if(debugMode)System.out.println("has_extended_hdr no");
		}  
		if (has_extended_hdr) 
		{
			int headersize = raf.read() << 21 | raf.read() << 14 | raf.read() << 7 | raf.read();
			raf.skipBytes(headersize - 4);
		}
		boolean uses_synch = (header_tag_data[5] & 0x80) != 0 ? true : false;
		//===================================================id3 v2 tags full length
		int alltags_length = byteArrayToInt(tag_alltags_length) + id3v2_header_size;
		if(debugMode)System.out.println("alltags_length  "+alltags_length);
		//===================================================get id3v2 tags full data
		byte[] alltags_data = new byte[alltags_length];
		RandomAccessFile raf1 = new RandomAccessFile(target_file, "r");
		raf1.read(alltags_data, 0, alltags_length);
		ByteBuffer bBuf1 = ByteBuffer.allocate(alltags_length);
		bBuf1.put(alltags_data);
		bBuf1.rewind();		

		//===================================================Prepare to parse the tag
		// System.out.println("print alltags _data "+new String(alltags_data));
		int TagsLength = alltags_data.length;
		// =========================Recreate the tag if desynchronization is used inside; w need to replace 0xFF 0x00 with 0xFF
		if (uses_synch) 
		{
			int newpos = 0;
			byte[] newbuffer = new byte[TagsLength];
			for (int i = 0; i < alltags_data.length; i++) {
				if (i < alltags_data.length - 1 && (alltags_data[i] & 0xFF) == 0xFF && alltags_data[i + 1] == 0) {
					newbuffer[newpos++] = (byte) 0xFF;
					i++;
					continue;
				}
				newbuffer[newpos++] = alltags_data[i];
			}
			TagsLength = newpos;
			alltags_data = newbuffer;
		}
		//===================================================reading all frames in cycle
		final int ID3FrameSize = id3v2_tag_version < 3 ? 6 : 10;
		// delete header bytes from array
		alltags_data = Arrays.copyOfRange(alltags_data, 10, alltags_data.length); 
		Mp3Id3tagObjectId3v2 local_object = new Mp3Id3tagObjectId3v2();
		
		ByteArrayOutputStream headerDataByteStream = new ByteArrayOutputStream();
		headerDataByteStream.write(tag);
		headerDataByteStream.write(tag_version);
		headerDataByteStream.write(tag_subversion);
		headerDataByteStream.write(tag_flags);
		headerDataByteStream.write(tag_alltags_length);
		local_object.setHeaderBytes(headerDataByteStream.toByteArray());
		
		String title = null;
		String artist = null;
		String album = null;
		String year = null;
		String comment = null;
		String composer = null;
		String track = null;
		String publisher = null;
		String original_artist = null;
		String copyright = null;
		String attached_url = null;
		String encoder = null;

		if(debugMode)System.out.println("found id3v2_tag_version "+id3v2_tag_version);		
		int cycle_position = 0;
		while (true) {
			int rembytes = TagsLength - cycle_position;
			if(cycle_position == alltags_data.length)
			{ 
				if(debugMode)System.out.println("cycle_position == alltags_data.length - break");
				break;	
			}
			//================================================check the frame header?
			if (rembytes < ID3FrameSize) break;
			// if ( alltags_data[cycle_position] != 'A' || alltags_data[cycle_position] != 'T') // check frame existance	
			if (alltags_data[cycle_position] < 'A' | 
				 alltags_data[cycle_position] > 'Z'|
			  alltags_data[cycle_position+1] < 'A' |
			  alltags_data[cycle_position+1] > 'Z'|
			 alltags_data[cycle_position+2] < 'A' |
			  alltags_data[cycle_position+2] > 'Z') // check frame existance
			{ 
				if(debugMode)System.out.println("alltags_data[cycle_position] < 'A' || alltags_data[cycle_position] > 'Z' - break");
				break;
			}
			String framename;
			int framesize;
			if (id3v2_tag_version < 3)  // Frame name is 3 chars in pre-ID3v3 and 4 chars after
			{	
				framename = new String(alltags_data, cycle_position, 3);
				framesize = ((alltags_data[cycle_position + 5] & 0xFF) << 8) 
							| ((alltags_data[cycle_position + 4] & 0xFF) << 16) 
							| ((alltags_data[cycle_position + 3] & 0xFF) << 24);
			} 
			else 
			{	
				framename = new String(alltags_data, cycle_position, 4);
				framesize = (alltags_data[cycle_position + 7] & 0xFF) 
							| ((alltags_data[cycle_position + 6] & 0xFF) << 8)
							| ((alltags_data[cycle_position + 5] & 0xFF) << 16) 
							| ((alltags_data[cycle_position + 4] & 0xFF) << 24);
			}
			if(debugMode)System.out.println("cycle_position "+cycle_position);
			if(debugMode)System.out.println("found framename "+framename);
			if(debugMode)System.out.println("found framesize "+framesize);
			
			if (cycle_position + framesize > TagsLength) { System.out.println("cycle_position + framesize > length - break "); break;}
			if (framename.equals("TPE1") || framename.equals("TPE2") || framename.equals("TPE3") || framename.equals("TPE")) {
				if (artist == null) artist = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}
			if (framename.equals("TRCK")) {
				if (track == null) track = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}
			if (framename.equals("TPUB")) {
				if (publisher == null) publisher = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}
			if (framename.equals("TOPE")) {
				if (original_artist == null) original_artist = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}
			if (framename.equals("WCOP")) {
				if (copyright == null) copyright = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}
			if (framename.equals("WXXX")) {
				if (attached_url == null) attached_url = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}
			if (framename.equals("TENC")) {
				if (encoder == null) encoder = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}
			if (framename.equals("TIT2") || framename.equals("TIT")) {
				if (title == null) title = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}
			if ( framename.equals("TALB") ) {
				if (album == null) album = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}
			if ( framename.equals("TCOM") ) {
				if (composer == null) composer = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}
			if ( framename.equals("TYER") ) {
				if (year == null) year = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}
			if ( framename.equals("COMM") ) {
				if (comment == null) comment = parseId3v2TextField(alltags_data, cycle_position + ID3FrameSize, framesize);
			}  
			if ( framename.equals("APIC") & returnAttachedPicture ) {
				byte[] bytes_attached_picture = Arrays.copyOfRange(alltags_data, cycle_position + ID3FrameSize , alltags_data.length );
        		//Get frame data
                int refPoint = 0;
                String mimeType = null;
                //get the encoding type
                int encType = (bytes_attached_picture[refPoint++] & 0xFF); //0=ISO8859, 1=Unicode,2=UnicodeBE,3=UTF8
                //get the mime type
                int indexPoint = refPoint;
                while (bytes_attached_picture[refPoint++] != 0)
                {
                }
                int mimeLength = refPoint - indexPoint;
                if (mimeLength > 1)
                {
                    mimeType = new String(bytes_attached_picture, indexPoint, mimeLength - 1, "ISO-8859-1");
                }
                //get the picture type
                int picType = (bytes_attached_picture[refPoint++] & 0xFF);
                byte[] desBuf = null;
                
                List<Byte> list = new ArrayList<Byte>(){};
                if(debugMode)System.out.println("295 encType "+encType+" picType "+picType+" mimeType "+mimeType);
                if(debugMode)System.out.println("298 refPoint " + refPoint );
                switch (encType)
                {
                	case 0:
                    case 3:
                        //8bit string
                        byte num;
                        while ((refPoint < bytes_attached_picture.length) && ((num = bytes_attached_picture[refPoint++]) != 0x00))
                        {
                            list.add(num);
                        }
                        if(debugMode)System.out.println("list of desc bytes size "+list.size());
                        desBuf = new byte[list.size()];
                        for(int i=0;i<0;)
                        {
                        	desBuf[i] = list.get(i).byteValue();
                        	i++;
                        }
                        break;
                    case 1:
                    case 2:
                        //16bit string
                    	list = new ArrayList<Byte>(){};
                        do
                        {
                            byte item = bytes_attached_picture[refPoint++];
                            byte num2 = bytes_attached_picture[refPoint++];
                            if ((item == 0) && (num2 == 0x00))
                            {
                                break;
                            }
                            if (((item != 0xff) || (num2 != 0xfe)) || (encType != 1))
                            {
                                list.add(item);
                                list.add(num2);
                            }
                        }
                        while (refPoint < (bytes_attached_picture.length - 1));
                        if(debugMode)System.out.println("list of desc bytes size "+list.size());
                        desBuf = new byte[list.size()];
                        for(int i=0;i<0;)
                        {
                        	desBuf[i] = list.get(i).byteValue();
                        	i++;
                        }
                        break;
                    default:
                    	// do nothing 
                    	break;
                }
                String description = "";
                switch (encType)
                {
                    case 0:
                        description = new String(desBuf, "ISO-8859-1");
                        break;
                    case 1:
                    	description = new String(desBuf, "UTF-16");
                        break;
                    case 2:
                    	description = new String(desBuf, "UTF-16BE");
                        break;
                    case 3:
                    	description = new String(desBuf, "UTF-8");
                        break;
                }
                //get the image data
                if(debugMode)System.out.println("366 refPoint " + refPoint);
                if(debugMode)System.out.println("description " + description );
                int imCount = bytes_attached_picture.length - refPoint;
                byte[] new_attached_picture_bytes = new byte[imCount];
                new_attached_picture_bytes = Arrays.copyOfRange(bytes_attached_picture, refPoint , bytes_attached_picture.length);
                local_object.set_attachedpicture_id3v2(new_attached_picture_bytes);
			}
			cycle_position += framesize + ID3FrameSize;
			continue;
		}
		local_object.set_album_id3v2(album);
		local_object.set_author_id3v2(artist);
		local_object.set_comment_id3v2(comment);
		local_object.set_composer_id3v2(composer);
		local_object.set_title_id3v2(title);
		local_object.set_year_id3v2(year);
		local_object.setTagsSize(TagsLength);
		
		return local_object;
    }
	
    private String parseId3v1TextField(byte[] target_array) throws UnsupportedEncodingException {
    	String output_string = "";
    	UniversalDetector detector = new UniversalDetector(null);
    	detector.handleData(target_array, 0, target_array.length);
    	detector.dataEnd();
    	String encoding = detector.getDetectedCharset();
    	if(encoding != null)
    	{ 
    		output_string = new String(target_array,encoding.toLowerCase()); 
    		if(debugMode)System.out.println("detected encoding "+encoding); 
    	}
    	detector.reset();
	    return output_string;
	}
    
    private String getId3v1TextEncoding (byte[] target_array) throws UnsupportedEncodingException {
    	String encodingName = "";
    	UniversalDetector detector = new UniversalDetector(null);
    	detector.handleData(target_array, 0, target_array.length);
    	detector.dataEnd();
    	String encoding = detector.getDetectedCharset();
    	if(encoding != null)
    	{ 
    		encodingName = encoding.toLowerCase(); 
    		if(debugMode)System.out.println("detected encoding "+encoding); 
    	}
    	else 
    	{
    		encodingName = "utf8";
    	}
    	detector.reset();
	    return encodingName;
	}
    
	private String parseId3v2TextField( final byte[] buffer, int pos, int size )
	{
	    if ( size < 2 ){ return null;}	    
	    Charset charset;
	    int charcode = buffer[pos]; 
	    if(debugMode)System.out.println("392 char code --> "+ charcode);
	    byte[] tempArray = Arrays.copyOfRange(buffer, pos, pos+size);

	    switch (charcode)
	    {
	    case 0:
	    	//charset = Charset.forName( "ISO-8859-1" );
	    	charset = Charset.forName( "WINDOWS-1251" );
	    	break;
	    case 3:
	    	charset = Charset.forName( "UTF-8" );
	    	break;
	    default:
	    	charset = Charset.forName( "UTF-16" );
	    	break;	
	    }
	    String result = charset.decode( ByteBuffer.wrap( buffer, pos + 1, size - 1) ).toString();
	    if(debugMode)System.out.println("402 result string -- >"+ result);
	    return result;
	}

    public  int byteArrayToInt(byte[] b) {
    	ByteBuffer wrapped = ByteBuffer.wrap(b); // big-endian by default
    	int num = wrapped.getInt();
    	return num;
    }
}