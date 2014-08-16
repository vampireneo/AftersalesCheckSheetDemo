package hk.com.inchcape.aftersaleschecksheetdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.os.Handler;

public class LiveCardMenuActivity extends Activity {

    private final Handler mHandler = new Handler();

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Open the options menu right away.
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_AA1111:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startCheckSheet(getString(R.string.AA1111));
                    }
                });
                return true;
            case  R.id.action_BB1234:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startCheckSheet(getString(R.string.BB1234));
                    }
                });
                return true;
            case R.id.action_qrcode:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startScanQRCode();
                    }
                });
                return true;
            case R.id.action_stop:
                // Stop the service which will unpublish the live card.
                stopService(new Intent(this, AftersalesCheckSheetDemoService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        // Nothing else to do, finish the Activity.
        finish();
    }

    private void startCheckSheet(String regNo) {
        Intent mIntent = new Intent(this, MainActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString(getString(R.string.regNo), regNo);
        mIntent.putExtras(mBundle);
        startActivity(mIntent, mBundle);
    }

    private void startScanQRCode() {
        Intent mIntent = new Intent(this, QRCodeActivity.class);
        startActivity(mIntent);
    }
}
