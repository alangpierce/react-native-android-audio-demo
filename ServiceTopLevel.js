import React from 'react-native';
import BatchedBridge from 'BatchedBridge';
import { NativeModules } from 'react-native'

import { LinearLayout, TextView, ImageButton, processCallbacks } from 'react-android-remote-views'
import { pause, play } from './actions'
import store from './store'

// Adapted from https://github.com/reactjs/redux/issues/303#issuecomment-125184409
const observeStore = (handler) => {
    let oldState = store.getState();
    store.subscribe(() => {
        const newState = store.getState();
        handler(newState, oldState);
        oldState = newState;
    });
};

const setNotification = (notification) => {
    notification.customView = processCallbacks(notification.customView);
    NativeModules.StartService.invalidateNotification(
        JSON.stringify(notification));
};


const ServiceTopLevel = {
    initService: (foo) => {
        observeStore((newState, oldState) => {
            const isPlaying = newState.isPlaying;
            if (oldState.isPlaying != isPlaying) {
                // ic_media_pause and ic_media_play
                const iconResource = isPlaying ? 17301539 : 17301540;
                const action = isPlaying ? ServiceTopLevel.pause : ServiceTopLevel.play;

                const notification = {
                    isForeground: isPlaying,
                    customView: <LinearLayout backgroundColor="#000000"
                                              gravity="center">
                        <TextView text="Counting"
                                  textSize={20}
                                  textColor="#ffffff"/>
                        <ImageButton imageResource={iconResource}
                                     backgroundColor="#000000"
                                     onPress={action}/>
                    </LinearLayout>
                };
                setNotification(notification);
            }
        });
    },
    pause: () => {
        store.dispatch(pause());
    },
    play: () => {
        store.dispatch(play());
    }
};

BatchedBridge.registerCallableModule(
    'ServiceTopLevel',
    ServiceTopLevel
);

export default ServiceTopLevel;