package maderski.bluetoothautoplaymusic.helpers.enums

enum class MediaPlayers(val packageName: String) {
    GOOGLE_PLAY_MUSIC("com.google.android.music"),
    SPOTIFY("com.spotify.music"),
    PANDORA("com.pandora.android"),
    BEYOND_POD("mobi.beyondpod"),
    APPLE_MUSIC("com.apple.android.music"),
    FM_INDIA("com.fmindia.activities"),
    POWER_AMP("com.maxmpz.audioplayer"),
    DOUBLE_TWIST("com.doubleTwist.androidPlayer"),
    LISTEN_AUDIO_BOOK("com.acmeandroid.listen"),
    GOOGLE_PODCASTS("com.google.android.apps.podcasts"),
    DEEZERMUSIC("deezer.android.app"),
    POCKET_CASTS("au.com.shiftyjelly.pocketcasts"),
    RADIO_PARADISE("com.earthflare.android.radioparadisewidget.gpv2"),
    TUNE_IN_RADIO_PRO("radiotime.player"),
    FOOBAR_2000("com.foobar2000.foobar2000"),
    VANILLA_MUSIC("ch.blinkenlights.android.vanilla"),
    JIO_MUSIC("com.jio.media.jiobeats"),
    YOUTUBE_MUSIC("com.google.android.apps.youtube.music"),
    NOT_ON_LIST("not_on_list");

    companion object {
        fun getEnumForMediaPlayerPkg(packageName: String): MediaPlayers =
                values().firstOrNull { it.packageName == packageName } ?: NOT_ON_LIST
    }
}