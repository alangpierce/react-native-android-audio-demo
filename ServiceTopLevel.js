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


const ServiceTopLevel = {
    initService: (foo) => {
        observeStore((newState, oldState) => {
            const isPlaying = newState.isPlaying;
            if (oldState.isPlaying != isPlaying) {
                // ic_media_pause and ic_media_play
                const iconResource = isPlaying ? 17301539 : 17301540;
                const notification = {
                    customView: {
                        type: "LinearLayout",
                        properties: {
                            backgroundColor: "#000000",
                            gravity: "center"
                        },
                        children: [
                            {
                                type: "TextView",
                                properties: {
                                    text: "Counting",
                                    textSize: 20,
                                    textColor: "#ffffff",
                                },
                                children: [],
                                onClick: null,
                            },
                            {
                                type: "ImageButton",
                                properties: {
                                    // TODO: Make it possible to reference a
                                    // normal icon.
                                    imageResource: iconResource,
                                    backgroundColor: "#000000",
                                },
                                children: [],
                                onClick: null,
                            }
                        ],
                        onClick: null,
                    },
                };

                console.log("Making call to invalidateNotification");
                NativeModules.StartService.invalidateNotification(
                    isPlaying, JSON.stringify(notification));
            }
        });
    },
    pause: (foo) => {
        store.dispatch(pause());
    },
    play: (foo) => {
        store.dispatch(play());
    }
};

BatchedBridge.registerCallableModule(
    'ServiceTopLevel',
    ServiceTopLevel
);

export default ServiceTopLevel;