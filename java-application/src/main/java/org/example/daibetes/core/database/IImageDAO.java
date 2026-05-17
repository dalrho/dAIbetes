package org.example.daibetes.core.database;

import java.io.File;

public interface IImageDAO {
    // Custom image persistence method
    int createImage(File imageFile, int userId);
    byte[] getImageBytes(int imageId);
}
