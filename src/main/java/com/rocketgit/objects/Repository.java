package com.rocketgit.objects;

public class Repository {
    String name;
    String path;
    String url;

    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Repository(String name, String path) {
        setName(name);
        setPath(path);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return getName();
    }
}
