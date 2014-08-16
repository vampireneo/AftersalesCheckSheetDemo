package hk.com.inchcape.aftersaleschecksheetdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {
    private GestureDetector mGestureDetector;
    public static String TAG = "GLASS DEMO";
    private String aipDomain = "http://api.inchcape.com.hk";
    private String regNo = "";

    private int currentIndex;

    private List<Steps> mSteps;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.aftersales_check_sheet_demo_state);

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        currentIndex = -1;
        changeView(currentIndex);

        mGestureDetector = new GestureDetector(this);

        mGestureDetector.setBaseListener(new GestureDetector.BaseListener() {
                                             @Override
                                             public boolean onGesture(Gesture gesture) {
                                                 AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                                 if (gesture == Gesture.TAP) {
                                                     Log.v(TAG, "TAP");
                                                     am.playSoundEffect(Sounds.TAP);
                                                     openOptionsMenu();
                                                     return true;
                                                 } else if (gesture == Gesture.TWO_TAP) {
                                                     Log.v(TAG, "TWO_TAP");
                                                     am.playSoundEffect(Sounds.SUCCESS);
                                                     setCheck(currentIndex, true);
                                                     return true;
                                                 } else if (gesture == Gesture.LONG_PRESS) {
                                                     Log.v(TAG, "LONG_PRESS");
                                                     return true;
                                                 } else if (gesture == Gesture.SWIPE_RIGHT) {
                                                     Log.v(TAG, "SWIPE_RIGHT");
                                                     if (currentIndex < 0) return true;
                                                     if (++currentIndex >= mSteps.size()) currentIndex = mSteps.size() - 1;
                                                     changeView(currentIndex);
                                                     return true;
                                                 } else if (gesture == Gesture.SWIPE_LEFT) {
                                                     Log.v(TAG, "SWIPE_LEFT");
                                                     if (currentIndex < 0) return true;
                                                     if (--currentIndex < 0) currentIndex = 0;
                                                     changeView(currentIndex);
                                                     return true;
                                                 } else if (gesture == Gesture.SWIPE_DOWN) {
                                                     Log.v(TAG, "SWIPE_DOWN");
                                                     return false;
                                                 } else if (gesture == Gesture.SWIPE_UP) {
                                                     Log.v(TAG, "SWIPE_UP");
                                                     return true;
                                                 } else if (gesture == Gesture.THREE_LONG_PRESS) {
                                                     Log.v(TAG, "THREE_LONG_PRESS");
                                                     return true;
                                                 } else if (gesture == Gesture.THREE_TAP) {
                                                     Log.v(TAG, "THREE_TAP");
                                                     am.playSoundEffect(Sounds.DISALLOWED);
                                                     setCheck(currentIndex, false);
                                                     return true;
                                                 } else if (gesture == Gesture.TWO_LONG_PRESS) {
                                                     Log.v(TAG, "TWO_LONG_PRESS");
                                                     return true;
                                                 } else if (gesture == Gesture.TWO_SWIPE_DOWN) {
                                                     Log.v(TAG, "TWO_SWIPE_DOWN");
                                                     return false;
                                                 } else if (gesture == Gesture.TWO_SWIPE_LEFT) {
                                                     Log.v(TAG, "TWO_SWIPE_LEFT");
                                                     return true;
                                                 } else if (gesture == Gesture.TWO_SWIPE_RIGHT) {
                                                     Log.v(TAG, "TWO_SWIPE_RIGHT");
                                                     return true;
                                                 } else if (gesture == Gesture.TWO_SWIPE_UP) {
                                                     Log.v(TAG, "TWO_SWIPE_UP");
                                                     return true;
                                                 }

                                                 return false;
                                             }
                                         });
        regNo = getIntent().getExtras().getString(getString(R.string.regNo));
        new GetCheckSheetTask(this).execute(regNo);
    }

    private class GetCheckSheetTask extends AsyncTask<String, Void, ArrayList<Steps>> {
        public Activity activity;

        public GetCheckSheetTask(Activity a) {
            activity = a;
        }

        @Override
        protected ArrayList<Steps> doInBackground(String... regNo) {
            ArrayList<Steps> stepLists = new ArrayList<Steps>();
            try {
                JSONObject response = Json.getJson(aipDomain + "/api/v1/checksheet/" + regNo[0], "GET");
                JSONObject vehicleCheckSheet = (JSONObject) response.getJSONArray("VehicleCheckSheet").get(0);
                JSONObject checkSheet = vehicleCheckSheet.getJSONObject("CheckSheets");
                JSONArray steps = checkSheet.getJSONArray("Steps");
                for (int i = 0; i < steps.length(); i++) {
                    JSONObject step = (JSONObject) steps.get(i);
                    Steps instructionStep = new Steps();
                    instructionStep.instruction = step.getString("instruction");
                    instructionStep.section = step.getString("section");
                    instructionStep.imgPath = aipDomain + "/images/" + step.getString("img");
                    instructionStep.img = imageFetch(instructionStep.imgPath);
                    instructionStep.checked = false;
                    instructionStep.id = step.getString("id");
                    stepLists.add(instructionStep);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return stepLists;
        }

        @Override
        protected void onPostExecute(ArrayList<Steps> s) {
            mSteps = s;
            currentIndex = 0;
            changeView(currentIndex);
        }

        protected Bitmap imageFetch(String source) throws IOException {
            Bitmap x;
            HttpURLConnection connection = (HttpURLConnection) new URL(source).openConnection();
            connection.connect();

            InputStream input = connection.getInputStream();
            x = BitmapFactory.decodeStream(input);
            return x;
        }
    }

    private class PostCheckSheetTask extends AsyncTask<String, Void, Boolean> {
        public Activity activity;

        public PostCheckSheetTask(Activity a) {
            activity = a;
        }

        @Override
        protected Boolean doInBackground(String... regNo) {
            String data = "?data=";
            for(int i = 0; i < mSteps.size(); i++) {
                Steps step = mSteps.get(i);
                data += (i == 0 ? "" : "__") + step.id + "_"  + (step.checked ? "1" : "0");
            }
            String response = Json.getString(aipDomain + "/api/v1/checksheet/" + regNo[0] + data, "POST");
            return response.equals("true");
        }

        @Override
        protected void onPostExecute(Boolean s) {
            Toast.makeText(activity, "Record updated.", Toast.LENGTH_SHORT).show();
            finish();
            //currentIndex = -3;
            //changeView(currentIndex);
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(0).setVisible(currentIndex == -2);
        menu.getItem(1).setVisible(currentIndex == -2);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aftersales_check_sheet_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection. Menu items typically start another
        // activity, start a service, or broadcast another intent.
        switch (item.getItemId()) {
            case R.id.action_stop:
                finish();
                return true;
            case R.id.action_cancel:
                currentIndex = mSteps.size() - 1;
                changeView(currentIndex);
                return true;
            case R.id.action_confirm:
                new PostCheckSheetTask(this).execute(regNo);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setCheck(int id, boolean checked) {
        if (id >= 0 && id < mSteps.size()) {
            Steps step = mSteps.get(id);
            step.checked = checked;
        }
        currentIndex = id + 1;
        if (currentIndex >= mSteps.size()) {
            currentIndex = -2;
            //currentIndex = mSteps.size() - 1;
        }
        changeView(currentIndex);
    }

    private void changeView(int id) {
        if (id >= 0 && id < mSteps.size()) {
            setContentView(R.layout.aftersales_check_sheet_demo);
            ImageView img = (ImageView) findViewById(R.id.imageView);
            TextView instruction = (TextView) findViewById(R.id.instruction_text);
            TextView section = (TextView) findViewById(R.id.section_text);

            img.setVisibility(View.VISIBLE);
            Steps step = mSteps.get(id);
            img.setImageBitmap(step.img);
            instruction.setText(step.instruction);
            section.setText(step.section);
        }
        else {
            setContentView(R.layout.aftersales_check_sheet_demo_state);
            TextView state = (TextView) findViewById(R.id.state_text);
            if (id == -1) state.setText(getString(R.string.Loading));
            else if (id == -2) state.setText(getString(R.string.Confirm));
            else if (id == -3) state.setText(getString(R.string.Done));
        }
    }
}
