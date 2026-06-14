package parsemedia;

import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Parsemedia extends AndroidNonvisibleComponent {

    public Parsemedia(ComponentContainer container) {
        super(container.$form());
    }

    @SimpleFunction(description = "Returns true if the file is a video.")
    public boolean IsVideo(String filePath) {
        return isVideoFile(filePath);
    }

    @SimpleFunction(description = "Returns true if the file is a photo.")
    public boolean IsPhoto(String filePath) {
        return isPhotoFile(filePath);
    }

    @SimpleFunction(description = "Returns the creation date in yyyy:MM:dd HH:mm:ss format for a photo or video file. Returns an empty string if the date is not available.")
    public String GetDate(String filePath) {
        try {
            if (isVideoFile(filePath)) {
                return getVideoDate(filePath);
            }
            return getPhotoDate(filePath);
        } catch (IOException e) {
            return "";
        }
    }

    private static String getPhotoDate(String filePath) throws IOException {
        ExifInterface exif = new ExifInterface(new File(filePath));
        String date = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
        if (date == null || date.isEmpty()) {
            date = exif.getAttribute(ExifInterface.TAG_DATETIME);
        }
        if (date == null || date.isEmpty()) {
            date = exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);
        }
        return date == null ? "No date found" : date;
    }

    private static String getVideoDate(String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            String date = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            return date == null ? "No date found" : formatVideoDateToExif(date);
        } finally {
            retriever.release();
        }
    }

    private static boolean isPhotoFile(String filePath) {
        String lower = filePath.toLowerCase(Locale.US);
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".png") || lower.endsWith(".gif")
                || lower.endsWith(".webp") || lower.endsWith(".bmp")
                || lower.endsWith(".heic") || lower.endsWith(".heif")
                || lower.endsWith(".tif") || lower.endsWith(".tiff")
                || lower.endsWith(".dng") || lower.endsWith(".raw");
    }

    private static boolean isVideoFile(String filePath) {
        String lower = filePath.toLowerCase(Locale.US);
        return lower.endsWith(".mp4") || lower.endsWith(".mov")
                || lower.endsWith(".3gp") || lower.endsWith(".mkv")
                || lower.endsWith(".webm") || lower.endsWith(".avi");
    }

    private static String formatVideoDateToExif(String date) {
        if (date == null || date.trim().isEmpty()) {
            return "No date found";
        }
        date = date.trim();
        if (date.matches("\\d{4}:\\d{2}:\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            return date;
        }
        String[] patterns = {
                "yyyyMMdd'T'HHmmss",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd",
                "yyyyMMdd"
        };
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US);
        for (String pattern : patterns) {
            try {
                SimpleDateFormat inFormat = new SimpleDateFormat(pattern, Locale.US);
                inFormat.setLenient(false);
                Date parsed = inFormat.parse(date);
                if (parsed != null) {
                    return outFormat.format(parsed);
                }
            } catch (ParseException ignored) {
            }
        }
        return "No date found";
    }
}
