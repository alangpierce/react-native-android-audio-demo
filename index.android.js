/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */
'use strict';
import React, {
    AppRegistry,
    Component,
    NativeModules,
    StyleSheet,
    Text,
    TouchableNativeFeedback,
    View
} from 'react-native';

import { createStore, applyMiddleware } from 'redux'
import thunk from 'redux-thunk'
import { connect, Provider } from 'react-redux'
import tts from 'react-native-android-speech'
import ServiceTopLevel from './ServiceTopLevel';


const SET_PLAYING = 'SET_PLAYING';
const SET_PAUSED = 'SET_PAUSED';
const INCREMENT_COUNTER = 'INCREMENT_COUNTER';

const incrementCounter = () => {
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

const play = () => {
    return (dispatch, getState) => {
        const existingTimerId = getState().timerId;
        // Play should be idempotent; if there is already a timer, just drop the
        // event, and assume by invariant that it's going.
        if (existingTimerId !== null) {
            return;
        }
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

const pause = () => {
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

const toggleState = () => {
    return (dispatch, getState) => {
        if (getState().isPlaying) {
            dispatch(pause());
        } else {
            dispatch(play());
        }
    }
};

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

const store = createStore(
    numberCounterApp,
    applyMiddleware(thunk)
);

class NumberCounter extends Component {
    render() {
        return <Provider store={store}>
            <NumberCounterContainer />
        </Provider>
    }
}

/**
 * Presentational component.
 */
const NumberCounterUI = ({isPlaying, value, onPress}) => (
    <View style={styles.container}>
        <Text style={styles.welcome} onPress={onPress}>
            {isPlaying ? "Pause" : "Play"}
        </Text>

        <Text style={styles.instructions}>
            {value}
        </Text>
    </View>
);

const mapStateToProps = (state) => {
    return {
        isPlaying: state.isPlaying,
        value: state.value,
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        onPress: () => {
            dispatch(toggleState());
        }
    };
};


const NumberCounterContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(NumberCounterUI);

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});

console.log("Hello from JS land!");

AppRegistry.registerComponent('NumberCounter', () => NumberCounter);
