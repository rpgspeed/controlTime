package com.roberto.controltime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.roberto.controltime.entity.Registry;
import com.roberto.controltime.service.RegistryService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button entry;
    private Button exit;
    private TextView valueLastAccess;
    private TextView valueDifferenceDay;

    private RegistryService registryService;

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        entry = (Button) findViewById(R.id.entrada);
        exit = (Button) findViewById(R.id.salida);
        valueLastAccess = (TextView) findViewById(R.id.valueLastRegistry);
        valueDifferenceDay = (TextView) findViewById(R.id.valueDifferenceDay);

        registryService = new RegistryService(getApplicationContext());

        recalculateView();

        startRefresh();
    }

    private void startRefresh() {
        final ScheduledFuture<?> schedule = scheduledExecutorService.scheduleAtFixedRate(new ScheduleUpdateUI(), 0, 1, TimeUnit.SECONDS);
    }


    public void recalculateView() {
        Registry registry = registryService.getLastAccess();

        if (registry != null && registry.isEntry()){
            makeEntry(entry);
        }else {
            makeEntry(exit);
        }
        if (registry != null) {
            valueLastAccess.setText(registryService.getLastAccessDate());
        }
    }

    public void registryEntry(View v) {
        registryService.registryEntry();
//        makeEntry(v);
        recalculateView();
    }

    public void registryExit(View v) {
        registryService.registryExit();
//        makeEntry(v);
        recalculateView();
    }


    private void makeEntry(View v) {
        if (v.equals(entry)) {
            entry.setEnabled(false);
            exit.setEnabled(true);
        }else {
            entry.setEnabled(true);
            exit.setEnabled(false);
        }
    }

    private class UpdateUIRunnable implements Runnable {
        @Override
        public void run() {
            Registry lastAccess = registryService.getLastAccess();
            if (lastAccess != null) {
                valueDifferenceDay.setText(registryService.getRemainingDay());
            } else {
                valueDifferenceDay.setText("08:45");
            }
        }
    }

    private class ScheduleUpdateUI implements Runnable {
        @Override
        public void run() {
            runOnUiThread(new UpdateUIRunnable());

        }
    }
}
