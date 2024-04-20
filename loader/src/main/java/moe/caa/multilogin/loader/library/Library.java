package moe.caa.multilogin.loader.library;

import java.io.File;

public record Library(
        String group,
        String name,
        String version
) {
    public static Library of(String str) {
        String[] strings = str.split(":");
        return new Library(strings[0], strings[1], strings[2]);
    }

    public File getFile(File folder) {
        return new File(folder, getUrl());
    }

    public String getUrl() {
        return group.replace(".", "/")
                + "/" + name + "/" + version + "/"
                + getFileName();
    }

    public String getFileName() {
        return name + "-" + version + ".jar";
    }

    public String getDisplayName() {
        return group + ":" + name + ":" + version;
    }

    public File getFileRelocated(File folder) {
        String url = getUrl();
        url = url.substring(0, url.length() - 4) + "-relocated.jar";
        return new File(folder, url);
    }
}

