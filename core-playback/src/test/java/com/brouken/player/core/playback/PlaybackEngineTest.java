package com.brouken.player.core.playback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class PlaybackEngineTest {

    @Test
    public void buildBasicAuthHeaders_returnsNull_forNullUri() {
        assertNull(PlaybackEngine.buildBasicAuthHeaders(null));
    }

    @Test
    public void buildBasicAuthHeaders_returnsNull_forNonHttpScheme() {
        Uri uri = Uri.parse("smb://user:pass@host/share/file.mkv");
        assertNull(PlaybackEngine.buildBasicAuthHeaders(uri));
    }

    @Test
    public void buildBasicAuthHeaders_returnsNull_whenNoUserInfo() {
        Uri uri = Uri.parse("http://host/file.mkv");
        assertNull(PlaybackEngine.buildBasicAuthHeaders(uri));
    }

    @Test
    public void buildBasicAuthHeaders_returnsNull_whenUserInfoHasNoColon() {
        Uri uri = Uri.parse("http://justauser@host/file.mkv");
        assertNull(PlaybackEngine.buildBasicAuthHeaders(uri));
    }

    @Test
    public void buildBasicAuthHeaders_buildsAuthorizationHeader_forHttp() {
        Uri uri = Uri.parse("http://user:pass@host/file.mkv");
        Map<String, String> headers = PlaybackEngine.buildBasicAuthHeaders(uri);

        assertTrue(headers.containsKey("Authorization"));
        assertTrue(headers.get("Authorization").startsWith("Basic "));
    }

    @Test
    public void buildBasicAuthHeaders_buildsAuthorizationHeader_forHttps() {
        Uri uri = Uri.parse("https://user:pass@host/file.mkv");
        Map<String, String> headers = PlaybackEngine.buildBasicAuthHeaders(uri);

        assertEquals("Basic dXNlcjpwYXNz", headers.get("Authorization"));
    }
}
