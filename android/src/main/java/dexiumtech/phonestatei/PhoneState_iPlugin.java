package dexiumtech.phonestatei;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telecom.TelecomManager;
import android.util.Log;

//import android.os.Bundle;
//import android.os.Environment;
//import static android.Manifest.permission.READ_PHONE_STATE;
import android.content.Context;


/** PhoneState_iPlugin */
public class PhoneState_iPlugin implements EventChannel.StreamHandler {

    private static final String PHONE_STATE =
            "PHONE_STATE_99";


    /** Plugin registration. */
    public static void registerWith(Registrar registrar) {

        final EventChannel phoneStateCallChannel =
                new EventChannel(registrar.messenger(), PHONE_STATE);
        phoneStateCallChannel.setStreamHandler(
                new PhoneState_iPlugin(registrar.context()));

    }

    private PhoneStateListener mPhoneListener;
    private final TelephonyManager telephonyManager;
    private final TelecomManager telecomManager;

    /** flag used for state */
    public static Boolean phoneCallOn=false;


    /** telephone manager */
    private PhoneState_iPlugin(Context context) {
        telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        telecomManager = (TelecomManager) context.getSystemService(context.TELECOM_SERVICE);
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        mPhoneListener = createPhoneStateListener(events, arguments.toString());
        telephonyManager.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onCancel(Object arguments) {
        ///
    }

    PhoneStateListener createPhoneStateListener(final EventChannel.EventSink events, final String arguments){
        return new PhoneStateListener(){
            @Override
            public void onCallStateChanged (int state, String phoneNumber){
                if(state == TelephonyManager.CALL_STATE_IDLE){
                    phoneCallOn = false;
                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    phoneCallOn = true;
                    if(phoneNumber.equals(arguments)){
                        try{
                            if (telecomManager != null) {
                              boolean success = telecomManager.endCall();
                              // success == true if call was terminated.
                            }
                          } catch(Exception e){
                            Log.d("",e.getMessage());
                          }
                    }
                    
                } else if(state == TelephonyManager.CALL_STATE_RINGING) {
                    phoneCallOn = true;
                } 
                events.success(phoneNumber);
            }
        };
    }

}
