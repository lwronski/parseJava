public class Images {

    private String url;
    private int size;


    public Images(String url, int size) {
        this.url = url;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return url + " : " + size;
    }
}
