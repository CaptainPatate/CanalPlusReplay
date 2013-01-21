package eu.amaurygauthier.canalplusreplay;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

public final class MediaPlayers {
	public final class PlayerInfo {
		public String name;
		public String pkg;
		public String fullyQualifiedName;
		public boolean isDefault = false;
	}

	private List<ResolveInfo> moviePlayers;
	private PackageManager pm;

	public MediaPlayers(PackageManager pm) {
		Intent play = new Intent(Intent.ACTION_DEFAULT);

		// setType() does not find all media players. For some reasons, when we
		// add uri scheme, all of them are found. D8 seems to require rtmp :/
		play.setDataAndType(Uri.parse("rtmp://foo"), "video/mp4");

		this.pm = pm;
		moviePlayers = pm.queryIntentActivities(play,
				PackageManager.GET_INTENT_FILTERS);
	}

	public List<MediaPlayers.PlayerInfo> getMediaPlayers() {
		List<MediaPlayers.PlayerInfo> res = new ArrayList<MediaPlayers.PlayerInfo>(
				5);

		for (ResolveInfo r : moviePlayers) {
			PlayerInfo pi = new PlayerInfo();

			pi.name = r.loadLabel(pm).toString();
			pi.pkg = r.activityInfo.applicationInfo.packageName;
			pi.fullyQualifiedName = pi.pkg + "/" + r.activityInfo.applicationInfo.className;
			if (r.isDefault)
				pi.isDefault = true;

			res.add(pi);
			Log.d(MediaPlayers.class.getName(), "Found this media player: "+pi.fullyQualifiedName);
		}

		return res;
	}
}