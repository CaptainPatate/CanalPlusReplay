package eu.amaurygauthier.canalplusreplay;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import java.util.List;

public class SettingsActivity extends Activity {
	private static PackageManager pm;

	public static final String KEY_PLAYERS = "players";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pm = getPackageManager();

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();

	}

	public static final class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);

			ListPreference listPref = (ListPreference) findPreference("players");
			if (listPref != null) {
				List<MediaPlayers.PlayerInfo> mp = new MediaPlayers(pm)
						.getMediaPlayers();

				CharSequence entries[] = new String[mp.size()];
				CharSequence entryValues[] = new String[mp.size()];
				int i = 0;
				for (MediaPlayers.PlayerInfo mpInfos : mp) {
					entries[i] = mpInfos.name;
					entryValues[i] = mpInfos.pkg;
					i++;
				}
				listPref.setEntries(entries);
				listPref.setEntryValues(entryValues);
				
				//set default value
				for (MediaPlayers.PlayerInfo mpInfos: mp) {
					if (mpInfos.isDefault)
						listPref.setValue(mpInfos.pkg);
				}
			}
		}
	}
}
