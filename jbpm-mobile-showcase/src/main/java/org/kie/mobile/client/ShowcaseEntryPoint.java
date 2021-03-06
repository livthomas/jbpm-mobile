/*
 * Copyright 2014 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.mobile.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.mvp.client.AnimatableDisplay;
import com.googlecode.mgwt.mvp.client.AnimatingActivityManager;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import javax.inject.Inject;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.mobile.client.home.HomePlace;

/**
 *
 * @author salaboy
 * @author livthomas
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private ClientFactory clientFactory;
    
    @Inject
    private PhoneActivityMapper activityMapper;
    
    @Inject
    private PhoneAnimationMapper animationMapper;

    @AfterInitialization
    public void startApp() {
        hideLoadingPopup();
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {
                Window.alert("uncaught: " + e.getMessage());
                e.printStackTrace();
            }
        });
        new Timer() {

            @Override
            public void run() {
                start();

            }
        }.schedule(1);
    }

    private void start() {
        MGWTSettings settings = MGWTSettings.getAppSetting();
        settings.setFullscreen(true);
        settings.setPreventScrolling(true);
        MGWT.applySettings(settings);

        // create display
        AnimatableDisplay display = GWT.create(AnimatableDisplay.class);
//        display.setSecondWidget(clientFactory.getHomePresenter().getView());
        AnimatingActivityManager activityManager = new AnimatingActivityManager(activityMapper, animationMapper,
                clientFactory.getEventBus());
        activityManager.setDisplay(display);
        RootPanel.get().add(display);
        
        // visit home page
        clientFactory.getPlaceController().goTo(new HomePlace());
    }

    private void hideLoadingPopup() {
        final Element e = RootPanel.get("loading").getElement();

        new com.google.gwt.animation.client.Animation() {
            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility(Style.Visibility.HIDDEN);
            }
        }.run(500);
    }

    public static native void redirect(String url)/*-{
     $wnd.location = url;
     }-*/;

}
