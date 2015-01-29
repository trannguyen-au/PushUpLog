package mobile.wnext.pushupsdiary.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mobile.wnext.pushupsdiary.Constants;
import mobile.wnext.pushupsdiary.R;

public class AboutActivity extends ActionBarActivity
    implements View.OnClickListener{

    Button btnContact, btnRemoveAds;
    TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        btnContact = (Button) findViewById(R.id.btnContact);
        btnContact.setOnClickListener(this);

        btnRemoveAds = (Button) findViewById(R.id.btnRemoveAds);
        btnRemoveAds.setOnClickListener(this);

        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvMessage.setText(Html.fromHtml(getResources().getString(R.string.privacy_statement)));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onClick(View view) {
        if(view == btnContact) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:nguyennt86@gmail.com?subject=Push Up Diary Android App")));
        }
        else if(view == btnRemoveAds) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ Constants.PRO_PACKAGE_NAME)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Constants.PRO_PACKAGE_NAME)));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
