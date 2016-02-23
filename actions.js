import { NativeModules } from 'react-native'
import tts from 'react-native-android-speech'

export const SET_PLAYING = 'SET_PLAYING';
export const SET_PAUSED = 'SET_PAUSED';
export const INCREMENT_COUNTER = 'INCREMENT_COUNTER';

export const incrementCounter = () => {
    return (dispatch, getState) => {
        // TODO: This is a bit ugly, but should work: we know the current value,
        // so we can just add one to get the new value to speak.
        const newValue = getState().value + 1;
        tts.speak({
            text: '' + newValue,
        });
        dispatch({
            type: INCREMENT_COUNTER,
        })
    };
};

export const play = () => {
    return (dispatch, getState) => {
        const existingTimerId = getState().timerId;
        // Play should be idempotent; if there is already a timer, just drop the
        // event, and assume by invariant that it's going.
        if (existingTimerId !== null) {
            return;
        }
        NativeModules.StartService.startService();
        const timerId = setInterval(
            () => dispatch(incrementCounter()),
            1000
        );
        dispatch({
            type: SET_PLAYING,
            timerId: timerId,
        })
    }
};

export const pause = () => {
    return (dispatch, getState) => {
        const existingTimerId = getState().timerId;
        if (existingTimerId !== null) {
            clearInterval(existingTimerId);
            dispatch({
                type: SET_PAUSED,
            });
        }
    }
};

export const toggleState = () => {
    return (dispatch, getState) => {
        if (getState().isPlaying) {
            dispatch(pause());
        } else {
            dispatch(play());
        }
    }
};