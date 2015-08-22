package developer.gainwardeast.mp3tagger;

public class Mp3Id3tagObjectId3v2 {

	private byte[] headerBytes;
	private String id3v2_title = "";
	private String id3v2_author = "";
	private String id3v2_album = "";
	private String id3v2_composer = "";
	private String id3v2_year = "";
	private String id3v2_comment = "";
	private String id3v2_track = "";
	private String id3v2_publisher = "";
	private String id3v2_original_artist = "";
	private String id3v2_copyright = "";
	private String id3v2_attached_url = "";
	private String id3v2_encoder = "";
	private int tagsSize = 0;
	
	private byte[] attached_picture = null;
	
	public void setTagsSize(int size)
	{
		this.tagsSize = size;
	}
	public int getTagsSize()
	{
		return this.tagsSize;
	}
	
	public void setHeaderBytes(byte[] array)
	{
		this.headerBytes = array;
	}
	public byte[] getHeaderBytes()
	{
		return this.headerBytes;
	}
	//=====================================================================id3v2 tags
	public void set_title_id3v2(String title)
	{
		if(title != null)this.id3v2_title = title;
	}
	public String get_title_id3v2()
	{
		return this.id3v2_title;
	}
	
	public void set_author_id3v2(String author)
	{
		if(author != null)this.id3v2_author = author;
	}
	public String get_author_id3v2()
	{
		return this.id3v2_author;
	}
	
	public void set_album_id3v2(String album)
	{
		if(album != null )this.id3v2_album = album;
	}
	public String get_album_id3v2()
	{
		return this.id3v2_album;
	}
	
	public void set_composer_id3v2(String composer)
	{
		if(composer != null)this.id3v2_composer = composer;
	}
	public String get_composer_id3v2()
	{
		return this.id3v2_composer;
	}
	
	public void set_year_id3v2(String year)
	{
		if(year != null)this.id3v2_year = year;
	}
	public String get_year_id3v2()
	{
		return this.id3v2_year;
	}
	
	public void set_comment_id3v2(String comment)
	{
		if(comment != null)this.id3v2_comment = comment;
	}
	public String get_comment_id3v2 ()
	{
		return this.id3v2_comment;
	}
	
	public void set_tracknumber_id3v2(String track)
	{
		if(track != null)this.id3v2_track = track;
	}
	public String get_tracknumber_id3v2 ()
	{
		return this.id3v2_track;
	}
	
	public void set_publisher_id3v2(String publisher)
	{
		if(publisher != null)this.id3v2_publisher = publisher;
	}
	public String get_publisher_id3v2 ()
	{
		return this.id3v2_publisher;
	}
	
	public void set_original_artist_id3v2(String original_artist)
	{
		if(original_artist != null)this.id3v2_original_artist = original_artist;
	}
	public String get_original_artist_id3v2 ()
	{
		return this.id3v2_original_artist;
	}
	
	public void set_copyright_id3v2(String copyright)
	{
		if(copyright != null)this.id3v2_copyright = copyright;
	}
	public String get_copyright_id3v2 ()
	{
		return this.id3v2_copyright;
	}
	
	public void set_attached_url_id3v2(String attached_url)
	{
		if(attached_url != null )this.id3v2_attached_url = attached_url;
	}
	public String get_attached_url_id3v2 ()
	{
		return this.id3v2_attached_url;
	}
	
	public void set_encoder_id3v2(String encoder)
	{
		if(encoder != null)this.id3v2_encoder = encoder;
	}
	public String get_encoder_id3v2 ()
	{
		return this.id3v2_encoder;
	}
	
	public void set_attachedpicture_id3v2(byte[] array)
	{
		this.attached_picture = array;
	}
	public byte[] get_attachedpicture_id3v2()
	{
		return this.attached_picture;
	}
}
