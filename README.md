# mp3tagger
Open library for reading id3 tags from .mp3 files

You can get access to tags doing this

Mp3TagsFactory factory = new Mp3TagsFactory();
factory.readTags(new File(PATH_TO_YOUR_FILE),true); 

second argument is boolean flag - do you want to read attached picture or not

# get access to tags by taking public variables from Factory class

factory.attached_picture
factory.author and others