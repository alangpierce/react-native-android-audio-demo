import { SET_PLAYING, SET_PAUSED, INCREMENT_COUNTER } from './actions'

const initialState = {
    value: 0,
    isPlaying: false,
    timerId: null,
};

const numberCounterApp = (state = initialState, action) => {
    switch (action.type) {
        case SET_PLAYING:
            return {
                ...state,
                isPlaying: true,
                timerId: action.timerId,
            };
        case SET_PAUSED:
            return {
                ...state,
                isPlaying: false,
                timerId: null,
            };
        case INCREMENT_COUNTER:
            return {
                ...state,
                value: state.value + 1,
            };
    }
    return state;
};

export default numberCounterApp;