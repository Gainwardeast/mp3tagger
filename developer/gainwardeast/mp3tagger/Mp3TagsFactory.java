package developer.gainwardeast.mp3tagger;

import java.io.File;
import java.io.IOException;

import developer.gainwardeast.mp3tagger.Mp3Id3tagObjectId3v1;
import developer.gainwardeast.mp3tagger.Mp3Id3tagObjectId3v2;
import developer.gainwardeast.mp3tagger.Mp3Id3tagParser;

public class Mp3TagsFactory {
	
	public String title = "";
	public String author = "";
	public String album = "";
	public String composer = "";
	public String year = "";
	public String comment = "";
	public String track = "";
	public String publisher = "";
	public String original_artist = "";
	public String copyright = "";
	public String attached_url = "";
	public String encoder = "";
	public byte[] attached_picture = null;
	
	public Mp3TagsFactory()
	{
		
	}
	
	public void readTags(File target_file,boolean readAttachedPicture)
	{
		Mp3Id3tagParser parser = new Mp3Id3tagParser(target_file,false);
		Mp3Id3tagObjectId3v1 id3v1_object = null;
		Mp3Id3tagObjectId3v2 id3v2_object = null;
		try {
			id3v1_object = parser.readTagId3v1();
			id3v2_object = parser.readTagId3v2(readAttachedPicture);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(id3v2_object != null)
		{
			title = id3v2_object.get_title_id3v2();
			author = id3v2_object.get_author_id3v2();
			album = id3v2_object.get_album_id3v2();
			composer = id3v2_object.get_composer_id3v2();
			year = id3v2_object.get_year_id3v2();
			comment = id3v2_object.get_comment_id3v2();
			track = id3v2_object.get_tracknumber_id3v2();
			publisher = id3v2_object.get_publisher_id3v2();
			original_artist = id3v2_object.get_original_artist_id3v2();
			copyright = id3v2_object.get_copyright_id3v2();
			attached_url = id3v2_object.get_attached_url_id3v2();
			encoder = id3v2_object.get_encoder_id3v2();
			attached_picture = id3v2_object.get_attachedpicture_id3v2();
		}
		else if(id3v1_object != null)
		{
			title = id3v1_object.get_title_id3v1();
			author = id3v1_object.get_author_id3v1();
			album = id3v1_object.get_album_id3v1();
			year = id3v1_object.get_year_id3v1();
			comment = id3v1_object.get_comment_id3v1();
		}
		
	}
	public void resetTagsFactory()
	{
		title = "";
		author = "";
		album = "";
		composer = "";
		year = "";
		comment = "";
		track = "";
		publisher = "";
		original_artist = "";
		copyright = "";
		attached_url = "";
		encoder = "";
		attached_picture = null;
	}
	
}