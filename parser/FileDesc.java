package parser;

public class FileDesc {
    
    private boolean open;
    private boolean append;
    private String path;

    public FileDesc(String path, boolean append) {
        this.path = path;
        this.open = true;
        this.append = append;
    }

    public void Open() {
        this.open = true;
    }

    public void Close() {
        this.open = false;
    }

    public boolean isOpen() {
        return this.open;
    }

    public String getPath() {
        return this.path;
    }

    public boolean shouldAppend() {
        return this.append;
    }
}
