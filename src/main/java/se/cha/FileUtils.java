package se.cha;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import com.drew.metadata.mp4.Mp4Directory;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public abstract class FileUtils {

    private FileUtils() {
    }

    public static Date getCreatedDate(File file) {
        final BasicFileAttributes attr = getBasicFileAttributes(file);
        return (attr != null) ? new Date(attr.creationTime().toMillis()) : null;
    }

    public static Date getModifiedDate(File file) {
        final BasicFileAttributes attr = getBasicFileAttributes(file);
        return (attr != null) ? new Date(attr.lastModifiedTime().toMillis()) : null;

    }

    private static BasicFileAttributes getBasicFileAttributes(File file) {
        BasicFileAttributes attr = null;
        try {
            final Path path = Paths.get(file.getAbsolutePath());
            attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return attr;
    }

    public static Date getImageExifDate(File imageFile) {
        Date imageExifDate = null;

        // Read image date from EXIF
        try {
            final Metadata metadata = readImageMetadata(imageFile);
            final ExifSubIFDDirectory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifIFD0Directory != null) {
                imageExifDate = exifIFD0Directory.getDateOriginal(TimeZone.getDefault());
            }
        } catch (ImageProcessingException | IOException e) {
            e.printStackTrace();
        }
        return imageExifDate;
    }

    public static Date getImageDateFromNefFile(File nefFile) {
        Date imageExifDate = null;

        // Read image date from EXIF
        try {
            final Metadata metadata = readImageMetadata(nefFile);
            final ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (exifIFD0Directory != null) {
                imageExifDate = exifIFD0Directory.getDate(ExifIFD0Directory.TAG_DATETIME_ORIGINAL, TimeZone.getDefault());
            }
        } catch (ImageProcessingException | IOException e) {
            e.printStackTrace();
        }
        return imageExifDate;
    }

    public static Date getImageDateFromXmpFile(File xmpFile) {
        Date imageExifDate = null;

        // Read image date from xmp xml-file.
        try {

            final Map<String, String> namespaces = new HashMap<>();
            namespaces.put("x", "adobe:ns:meta/");
            namespaces.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            namespaces.put("dmf", "http://www.bibblelabs.com/DigitalMasterFile/1.0/");
            namespaces.put("dmfversion", "http://www.bibblelabs.com/DigitalMasterFileVersion/1.0/");
            namespaces.put("bset", "http://www.bibblelabs.com/BibbleSettings/5.0/");
            namespaces.put("blay", "http://www.bibblelabs.com/BibbleLayers/5.0/");
            namespaces.put("bopt", "http://www.bibblelabs.com/BibbleOpt/5.0/");

            final DocumentFactory factory = new DocumentFactory();
            factory.setXPathNamespaceURIs(namespaces);

            final SAXReader saxBuilder = new SAXReader();
            saxBuilder.setDocumentFactory(factory);

            final Document document = saxBuilder.read(xmpFile);
            final Element rootElement = document.getRootElement();

            final String  xmpDateText = rootElement.valueOf( "/x:xmpmeta/rdf:RDF/rdf:Description/dmf:versions/rdf:Seq/rdf:li/rdf:Description/dmfversion:settings/rdf:Description/bset:layers/rdf:Seq/rdf:li/rdf:Description/blay:options/@bopt:Date" );
            final String  xmpDigitizedDateText = rootElement.valueOf( "/x:xmpmeta/rdf:RDF/rdf:Description/dmf:versions/rdf:Seq/rdf:li/rdf:Description/dmfversion:settings/rdf:Description/bset:layers/rdf:Seq/rdf:li/rdf:Description/blay:options/@bopt:DigitizedDateTime" );

            final String dateText = StringUtils.isNotBlank(xmpDateText) ? xmpDateText.trim() : StringUtils.isNotBlank(xmpDigitizedDateText) ? xmpDigitizedDateText.trim() : null;

            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            imageExifDate = StringUtils.isNotBlank(dateText) ? dateFormat.parse(dateText) : null;

        } catch (DocumentException | ParseException e) {
            e.printStackTrace();
        }

        return imageExifDate;
    }

    public static Date getImageDateFromAaeFile(File aaeFile) {
        Date imageExifDate = null;

        // Read image date from aae xml-file.
        try {
            final DocumentFactory factory = new DocumentFactory();

            final SAXReader saxBuilder = new SAXReader();
            saxBuilder.setDocumentFactory(factory);

            final Document document = saxBuilder.read(aaeFile);
            final Element rootElement = document.getRootElement();

            final String aaeDateText = rootElement.valueOf( "//dict/key[text()=\"adjustmentTimestamp\"]/following-sibling::date/text()");

            final OffsetDateTime imageAaeOffsetDateTime = StringUtils.isNotBlank(aaeDateText) ? OffsetDateTime.parse(aaeDateText) : null;

            if (imageAaeOffsetDateTime != null) {
                imageExifDate = new Date(imageAaeOffsetDateTime.toInstant().toEpochMilli());
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return imageExifDate;
    }

    public static Date getImageDateFromMovFile(File movFile) {
        Date date = null;

        try {
            final Metadata metadata = readImageMetadata(movFile);

            final QuickTimeMetadataDirectory quickTimeDirectory = metadata.getFirstDirectoryOfType(QuickTimeMetadataDirectory.class);
            if (quickTimeDirectory != null) {
                date = quickTimeDirectory.getDate(QuickTimeMetadataDirectory.TAG_CREATION_DATE, TimeZone.getDefault());
            }
        } catch (ImageProcessingException | IOException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static Date getImageDateFromMp4File(File mp4File) {
        Date date = null;

        try {
            final Metadata metadata = readImageMetadata(mp4File);

            final Mp4Directory quickTimeDirectory = metadata.getFirstDirectoryOfType(Mp4Directory.class);
            if (quickTimeDirectory != null) {
                date = quickTimeDirectory.getDate(Mp4Directory.TAG_CREATION_TIME, TimeZone.getDefault());
            }
        } catch (ImageProcessingException | IOException e) {
            e.printStackTrace();
        }

        return date;
    }


    private static Metadata readImageMetadata(File imageFile) throws ImageProcessingException, IOException {
        return ImageMetadataReader.readMetadata(imageFile);
    }

    public static boolean isMediaFile(File file) {
        final String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".heic")
                || fileName.endsWith(".gif")
                || fileName.endsWith(".nef")
                || fileName.endsWith(".mov")
                || fileName.endsWith(".mp4")
                || fileName.endsWith(".png")
                || fileName.endsWith(".xmp")
                || fileName.endsWith(".aae");
    }

    public static boolean isExifImageFile(File file) {
        final String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".gif")
                || fileName.endsWith(".png");
    }

    public static boolean isNefFile(File file) {
        final String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".nef");
    }

    public static boolean isMovFile(File file) {
        final String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mov");
    }

    public static boolean isMp4File(File file) {
        final String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp4");
    }

    public static boolean isXmpFile(File file) {
        final String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".xmp");
    }

    public static boolean isAaeFile(File file) {
        final String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".aae");
    }

    public static boolean isJpgImageFile(File file) {
        final String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg");
    }

    public static String byteCountToDisplaySize(long byteSize) {
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(byteSize);
    }

    public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        org.apache.commons.io.FileUtils.writeByteArrayToFile(file, data);
    }

    public static byte[] readFileToByteArray(File file) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToByteArray(file);
    }
}
