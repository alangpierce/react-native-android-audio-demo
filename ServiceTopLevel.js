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
        observeStore((oldState, newState) => {
            const isPlaying = newState.isPlaying;
            if (oldState.isPlaying != isPlaying) {
                NativeModules.StartService.invalidateNotification(isPlaying);
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