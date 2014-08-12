package hk.com.inchcape.aftersaleschecksheetdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private int currentIndex;

    private List<Steps> mSteps;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.aftersales_check_sheet_demo);

        currentIndex = -1;
        changeView(currentIndex);

        mGestureDetector = new GestureDetector(this);

        mGestureDetector.setBaseListener(new GestureDetector.BaseListener() {
                                             @Override
                                             public boolean onGesture(Gesture gesture) {
                                                 if (gesture == Gesture.TAP) {
                                                     Log.v(TAG, "TAP");
                                                     AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                                     am.playSoundEffect(Sounds.TAP);
                                                     openOptionsMenu();
                                                     return true;
                                                 } else if (gesture == Gesture.TWO_TAP) {
                                                     Log.v(TAG, "TWO_TAP");
                                                     return true;
                                                 } else if (gesture == Gesture.LONG_PRESS) {
                                                     Log.v(TAG, "LONG_PRESS");
                                                     return true;
                                                 } else if (gesture == Gesture.SWIPE_RIGHT) {
                                                     Log.v(TAG, "SWIPE_RIGHT");
                                                     if (++currentIndex >= mSteps.size()) currentIndex = mSteps.size() - 1;
                                                     changeView(currentIndex);
                                                     return true;
                                                 } else if (gesture == Gesture.SWIPE_LEFT) {
                                                     Log.v(TAG, "SWIPE_LEFT");
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
        mSteps = new ArrayList<Steps>();
        try {
            JSONObject response = Json.getJson("http://api.inchcape.com.hk/api/v1/checksheet/AA1111");
            Log.v(TAG, (response == null ? "is null" : "not null"));
            JSONObject vehicleCheckSheet = (JSONObject) response.getJSONArray("VehicleCheckSheet").get(0);
            JSONObject checkSheet = (JSONObject) vehicleCheckSheet.getJSONArray("CheckSheets").get(0);
            JSONArray steps = checkSheet.getJSONArray("Steps");
            for(int i = 0; i < steps.length(); i++) {
                JSONObject step = (JSONObject) steps.get(0);
                Steps instructionStep = new Steps();
                instructionStep.instruction = step.getString("instruction");
                instructionStep.section = step.getString("section");
                instructionStep.imgPath = step.getString("img");
                mSteps.add(instructionStep);
            }
        }
        catch (JSONException e) {
            
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
        //mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        //mCardScroller.deactivate();
        super.onPause();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeView(int id) {
        ImageView img = (ImageView) findViewById(R.id.imageView);
        TextView instruction = (TextView) findViewById(R.id.instruction_text);
        TextView section = (TextView) findViewById(R.id.section_text);
        Drawable drawable;
        img.setVisibility(View.VISIBLE);
        switch (id) {
            case 0:
                drawable = getResources().getDrawable(R.drawable.prius_v_1);
                img.setImageDrawable(drawable);
                break;
            case 1:
                drawable = getResources().getDrawable(R.drawable.prius_v_2);
                img.setImageDrawable(drawable);
                break;
            case 2:
                drawable = getResources().getDrawable(R.drawable.prius_v_3);
                img.setImageDrawable(drawable);
                break;
            case 3:
                drawable = getResources().getDrawable(R.drawable.prius_v_4);
                img.setImageDrawable(drawable);
                break;
            case 4:
                drawable = getResources().getDrawable(R.drawable.prius_v_5);
                img.setImageDrawable(drawable);
                break;
            case 5:
                drawable = getResources().getDrawable(R.drawable.prius_v_6);
                img.setImageDrawable(drawable);
                break;
            case 6:
                drawable = getResources().getDrawable(R.drawable.prius_v_7);
                img.setImageDrawable(drawable);
                break;
            case 7:
                drawable = getResources().getDrawable(R.drawable.prius_v_8);
                img.setImageDrawable(drawable);
                break;
            default:
                img.setVisibility(View.INVISIBLE);
                instruction.setText("");
                section.setText("");
                break;
        }
        if (id > 0 && id < mSteps.size()) {
            Steps step = mSteps.get(id);
            instruction.setText(step.instruction);
            section.setText(step.section);
        }
    }
}
