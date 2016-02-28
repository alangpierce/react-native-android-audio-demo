import React from 'react-native';
import BatchedBridge from 'BatchedBridge';
import { NativeModules } from 'react-native'

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


// TODO: Investigate if we could re-use the callback mechanism already provided
// by React Native.
const CALLBACKS_BY_ID = {};

const setNotification = (notification) => {
    notification.customView = processCallbacks(notification.customView);
    NativeModules.StartService.invalidateNotification(
        JSON.stringify(notification));
};

const processCallbacks = (view) => {
    const newProps = {};
    if (view.props.hasOwnProperty("onPress")) {
        newProps.onPress = generateIdForFunction(view.props.onPress);
        console.log("Set callback ID to " + view.props.onPress);
    }

    const newChildren = [];
    if (view.props.hasOwnProperty("children")) {
        view.props.children.forEach((child) => {
            newChildren.push(processCallbacks(child));
        });
    }
    return React.cloneElement(view, newProps, newChildren);
};


/**
 * Generate an ID for this function and track it so that we can look it up
 * later.
 */
const generateIdForFunction = (func) => {
    for (const callbackId in CALLBACKS_BY_ID) {
        // TODO: Yuck, this is a linear scan. Ideally we would keep a hash
        // table.
        if (CALLBACKS_BY_ID.hasOwnProperty(callbackId) &&
                CALLBACKS_BY_ID[callbackId] === func) {
            return callbackId;
        }
    }
    var newCallbackId = Math.random().toString(36).slice(2);
    CALLBACKS_BY_ID[newCallbackId] = func;
    console.log("Saved callback ID " + newCallbackId + " for function " + func);
    return newCallbackId;
};


const ServiceTopLevel = {
    initService: (foo) => {
        observeStore((newState, oldState) => {
            const isPlaying = newState.isPlaying;
            if (oldState.isPlaying != isPlaying) {
                // ic_media_pause and ic_media_play
                const iconResource = isPlaying ? 17301539 : 17301540;
                const action = isPlaying ? ServiceTopLevel.pause : ServiceTopLevel.play;

                const LinearLayout = "LinearLayout";
                const TextView = "TextView";
                const ImageButton = "ImageButton";

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
    runCallback: (callbackId) => {
        console.log("Invoking callback " + callbackId);
        CALLBACKS_BY_ID[callbackId]();
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