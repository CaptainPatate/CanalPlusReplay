package eu.amaurygauthier.canalplusreplay;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ViewVideo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		// TODO: we must provide a valid backup value if there are no config
		String player = sharedPref.getString(SettingsActivity.KEY_PLAYERS, "");

		Intent playerIntent = new Intent(Intent.ACTION_VIEW).setPackage(player);

		if (intent.getAction().equals(Intent.ACTION_SEND)) {
			// if this is from the share menu
			if (intent.hasExtra(Intent.EXTRA_TEXT)) {
				String vid = extras.getString(Intent.EXTRA_TEXT);

				Log.d(ViewVideo.class.getName(),
						"I must purify this link my commendent! " + vid);

				Pattern vidPattern = Pattern.compile(".*vid=(\\d+)");
				Matcher match = vidPattern.matcher(vid);

				if (match.matches()) {
					Log.d(ViewVideo.class.getName(),
							"I will find my princess here! " + match.group(1));

					String urlVid = null;
					try {
						urlVid = new AnalyzeXml().execute(match.group(1)).get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Log.d(ViewVideo.class.getName(),
							"There you can watch now! " + urlVid);

					if (urlVid != null)
						startActivity(playerIntent.setData(Uri.parse(urlVid)));
					
					finish();
				}
			}
		}
	}

	private final class AnalyzeXml extends AsyncTask<String, String, String> {
		private ProgressDialog dialog = new ProgressDialog(ViewVideo.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Analyzing...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			try {
				publishProgress("Parsing...");
				Document doc = factory.newDocumentBuilder().parse(
						"http://service.canal-plus.com/video/rest/getVideosLiees/cplus/"
								+ params[0]);

				publishProgress("Search in XML...");
				NodeList nList = doc.getElementsByTagName("HAUT_DEBIT");
				String vidUrl = nList.item(0).getFirstChild().getNodeValue();
				publishProgress("Found!");

				return vidUrl;

			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			dialog.setMessage(values[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			if (result == null) {
				Toast.makeText(ViewVideo.this, "Failed :( ", Toast.LENGTH_SHORT)
						.show();
			}

		}
	}
}
