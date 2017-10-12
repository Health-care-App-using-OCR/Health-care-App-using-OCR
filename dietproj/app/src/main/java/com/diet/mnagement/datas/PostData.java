package com.diet.mnagement.datas;



public class PostData
{
    private String writerUid;
    private String writerName;
    private String title;
    private String content;
    private String image;

    private String postId;

    public String getWriterUid()
    {
        return writerUid;
    }

    public void setWriterUid(String writerUid)
    {
        this.writerUid = writerUid;
    }

    public String getWriterName()
    {
        return writerName;
    }

    public void setWriterName(String writerName)
    {
        this.writerName = writerName;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }


    public String getPostId()
    {
        return postId;
    }

    public void setPostId(String postId)
    {
        this.postId = postId;
    }
}
