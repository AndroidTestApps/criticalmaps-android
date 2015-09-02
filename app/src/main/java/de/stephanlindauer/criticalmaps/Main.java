package de.stephanlindauer.criticalmaps;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import de.stephanlindauer.criticalmaps.adapter.TabsPagerAdapter;
import de.stephanlindauer.criticalmaps.handler.ApplicationCloseHandler;
import de.stephanlindauer.criticalmaps.handler.PrerequisitesChecker;
import de.stephanlindauer.criticalmaps.helper.CustomViewPager;
import de.stephanlindauer.criticalmaps.model.UserModel;
import de.stephanlindauer.criticalmaps.notifications.trackinginfo.TrackingInfoNotificationSetter;
import de.stephanlindauer.criticalmaps.service.LocationUpdatesService;
import de.stephanlindauer.criticalmaps.service.ServerSyncService;

public class Main extends FragmentActivity implements ActionBar.TabListener {

    //dependencies
    private final TrackingInfoNotificationSetter trackingInfoNotificationSetter = TrackingInfoNotificationSetter.getInstance();
    private final LocationUpdatesService locationUpdatesService = LocationUpdatesService.getInstance();
    private final UserModel userModel = UserModel.getInstance();

    //misc
    private CustomViewPager viewPager;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_main);

        setupViewPager();

        new PrerequisitesChecker(this).execute();

        initializeNotifications();

        locationUpdatesService.initialize(this);

        startSyncService();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra("shouldClose") && intent.getBooleanExtra("shouldClose", false)) {
            new ApplicationCloseHandler(this).execute();
        }
        super.onNewIntent(intent);
    }

    private void startSyncService() {
        Intent syncServiceIntent = new Intent(this, ServerSyncService.class);
        startService(syncServiceIntent);
    }

    private void initializeNotifications() {
        trackingInfoNotificationSetter.initialize(getBaseContext(), this);
        trackingInfoNotificationSetter.show();

        userModel.initialize(this);
    }

    private void setupViewPager() {
        viewPager = (CustomViewPager) findViewById(R.id.pager);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsPagerAdapter);

        actionBar.addTab(actionBar.newTab().setText(R.string.section_map).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.section_chat).setTabListener(this).setTag("chat_tab"));
        actionBar.addTab(actionBar.newTab().setText(R.string.section_twitter).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.section_rules).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.section_about).setTabListener(this));

        registerListenersForSwipedChanges(actionBar);
    }

    private void registerListenersForSwipedChanges(final ActionBar actionBar) {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    private void hideKeyBoard() {
        EditText editMessageTextfield = (EditText) findViewById(R.id.chat_edit_message);

        if (editMessageTextfield == null)
            return;

        editMessageTextfield.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editMessageTextfield.getWindowToken(), 0);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if("chat_tab".equals(tab.getTag())) {
            hideKeyBoard();
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

}
