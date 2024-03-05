import com.affirm.common.util.GzipUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class GzipUtilTest {

    @Test
    @Disabled
    public void zip_shouldThrowIllegalArgumentException_whenStringToCompressIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    GzipUtil.zip(null);
                });

    }

    @Test
    @Disabled
    public void zip_shouldThrowIllegalArgumentException_whenStringToCompressIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    GzipUtil.zip("");
                });

    }

    @Test
    public void zip_shouldGzipString_whenStringIsNotEmpty() {
        String xml = "<Hello>World</Hello>";

        byte[] actual = GzipUtil.zip(xml);

        assertTrue(GzipUtil.isZipped(actual));
    }


    @Test
    public void unzip_shouldThrowIllegalArgumentException_whenByteArrayToDecompressIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    GzipUtil.unzip(null);
                });

    }

    @Test
    public void unzip_shouldThrowIllegalArgumentException_whenByteArrayToDecompressIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    GzipUtil.unzip(new byte[0]);
                });

    }

    @Test
    public void unzip_shouldReturnInputByteArrayAsString_whenByteArrayContentIsNotGzipped() {
        String xml = "<Hello>World</Hello>";
        byte[] bytes = xml.getBytes();

        String actual = GzipUtil.unzip(bytes);
        assertEquals(xml, actual);
    }

    @Test
    public void unzip_shouldDecompressByteArrayGzippedContent() throws Exception {
        String xml = "<Hello>World</Hello>";
        byte[] compressed = GzipUtil.zip(xml);

        String actual = GzipUtil.unzip(compressed);

        assertEquals(xml, actual);
    }

    @Test
    public void isZipped_shouldReturnFalse_whenContentIsNotGzipped() {
        byte[] bytes = new byte[]{1, 2, 3};

        boolean actual = GzipUtil.isZipped(bytes);

        assertFalse(actual);
    }

    @Test
    public void isZipped_shouldReturnTrue_whenContentIsGzipped() {
        byte[] bytes = GzipUtil.zip("1,2,3");

        boolean actual = GzipUtil.isZipped(bytes);

        assertTrue(actual);
    }
}