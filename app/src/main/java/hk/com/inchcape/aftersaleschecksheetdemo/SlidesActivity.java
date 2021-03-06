package hk.com.inchcape.aftersaleschecksheetdemo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class SlidesActivity extends Activity {
    private GestureDetector mGestureDetector;
    public static String TAG = "GLASS DEMO";
    private String aipDomain = "http://api.inchcape.com.hk";
    private Activity me;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.slides_demo);
        TextView instruction = (TextView) findViewById(R.id.state_text);
        instruction.setText("Slides Remote Demo");

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        me = this;

        mGestureDetector = new GestureDetector(this);

        mGestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                //AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (gesture == Gesture.TAP) {
                    Log.v(TAG, "TAP");
                    //am.playSoundEffect(Sounds.TAP);
                    //openOptionsMenu();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    Log.v(TAG, "TWO_TAP");
                    //am.playSoundEffect(Sounds.SUCCESS);
                    return true;
                } else if (gesture == Gesture.LONG_PRESS) {
                    Log.v(TAG, "LONG_PRESS");
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    Log.v(TAG, "SWIPE_RIGHT");
                    new DoSlidesTask(me).execute(getString(R.string.next));
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    Log.v(TAG, "SWIPE_LEFT");
                    new DoSlidesTask(me).execute(getString(R.string.prev));
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
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class DoSlidesTask extends AsyncTask<String, Void, String> {
        public Activity activity;

        public DoSlidesTask(Activity a) {
            activity = a;
        }

        @Override
        protected String doInBackground(String... action) {
            String response = Json.getString(aipDomain + "/api/v1/slides/" + action[0], "GET");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(activity, "Action done: " + s, Toast.LENGTH_SHORT).show();
            s = s.replace("\"", "").replace("\n", "");
            if (s.equals("next") || s.equals("prev")) {
                TextView step = (TextView) findViewById(R.id.lastStep);
                step.setText("Last action: " + s);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new DoSlidesTask(activity).execute("notes");
                    }
                }, 500);
            }
            else {
                TextView instruction = (TextView) findViewById(R.id.state_text);
                instruction.setText("Notes: " + s);
            }
        }
    }

}
