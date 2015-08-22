package developer.gainwardeast.mp3tagger;

public class Mp3Id3tagObjectId3v1 {

	private String id3v1_title = "";
	private String id3v1_author= "";
	private String id3v1_album = "";
	private String id3v1_year = "";
	private String id3v1_comment = "";

	//=====================================================================id3v1	
	public void set_title_id3v1(String title)
	{
		if(title !=null)this.id3v1_title = title;
	}
	public String get_title_id3v1 ()
	{
		return this.id3v1_title;
	}
	
	public void set_author_id3v1(String author)
	{
		if(author != null)this.id3v1_author = author;
	}
	public String get_author_id3v1()
	{
		return this.id3v1_author;
	}
	
	public void set_album_id3v1(String album)
	{
		if(album != null)this.id3v1_album = album;
	}
	public String get_album_id3v1()
	{
		return this.id3v1_album;
	}
	
	public void set_year_id3v1(String year)
	{
		if(year != null)this.id3v1_year = year;
	}
	public String get_year_id3v1()
	{
		return this.id3v1_year;
	}
	
	public void set_comment_id3v1(String comment)
	{
		if(comment != null)this.id3v1_comment = comment;
	}
	public String get_comment_id3v1 ()
	{
		return this.id3v1_comment;
	}

}
