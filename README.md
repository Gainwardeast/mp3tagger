# mp3tagger
Open source library for reading id3 version 1 and 2 tags from .mp3 files

First do not forget to add universal char detector library created by mozilla.
Take it from "dependencies" folder

You can get access to tags doing this

```
Mp3TagsFactory factory = new Mp3TagsFactory();
factory.readTags(new File(PATH_TO_YOUR_FILE),true); 
```
second argument is boolean flag - do you want to read attached picture or not
# get access to tags by taking public variables from Factory class
```
factory.attached_picture
factory.author
```
 and etc.