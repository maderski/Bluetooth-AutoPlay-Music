package maderski.bluetoothautoplaymusic;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarAndTimeInfo GSS = new CalendarAndTimeInfo();
        GSS.getTimeZone();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            aboutSelected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Launches the AboutActivity when about is selected
    private void aboutSelected(){
        Toast.makeText(this, "Put about stuff here", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        radiobuttonCreator(this);
        checkboxCreator();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private boolean isBAPMServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BAPMService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void unlockTurnOnScreen(Context context){

        Window window = ((Activity) context).getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    private void sendAppToBackground(Context context){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(i);
    }

    private void listMusicplayersAndBTDevices(Context context){
        for(String pkg:VariousLists.listOfInstalledMediaPlayers(context)){
            Log.i("Pkg ", pkg);
        }

        for(String btDevice:VariousLists.listOfBluetoothDevices()){
            Log.i("BTDevice ", btDevice);
        }
    }

    private void checkboxCreator(){

        CheckBox checkBox;

        LinearLayout BTDeviceCkBoxLL = (LinearLayout) findViewById(R.id.checkBoxLL);
        BTDeviceCkBoxLL.removeAllViews();

        for(String BTDevice : VariousLists.listOfBluetoothDevices()){
            checkBox = new CheckBox(this);
            checkBox.setText(BTDevice);
            BTDeviceCkBoxLL.addView(checkBox);
        }

    }

    private void radiobuttonCreator(Context context){

        RadioButton rdoButton;
        ApplicationInfo appInfo;
        String mediaPlayer = "No Name";
        PackageManager pm = getPackageManager();

        RadioGroup rdoBTDevices = (RadioGroup) findViewById(R.id.rdoBTDevices);
        rdoBTDevices.removeAllViews();

        for(String packageName : VariousLists.listOfInstalledMediaPlayers(context)){

            try{
                appInfo = pm.getApplicationInfo(packageName, 0);
                mediaPlayer = pm.getApplicationLabel(appInfo).toString();

            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }

            rdoButton = new RadioButton(this);
            rdoButton.setText(mediaPlayer);
            rdoBTDevices.addView(rdoButton);

        }
    }

}
