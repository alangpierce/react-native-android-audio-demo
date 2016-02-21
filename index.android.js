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

import { createStore} from 'redux'
import { connect, Provider } from 'react-redux'
import tts from 'react-native-android-speech'
import ServiceTopLevel from './ServiceTopLevel';


const initialState = {
    value: 0,
    isPlaying: false,
    intervalId: null,
};

const numberCounterApp = (state = initialState, action) => {
    console.log("Got action " + JSON.stringify(action));
    console.log("Current state: " + JSON.stringify(state));
    switch (action.type) {
        case TOGGLE_STATE:
            return {
                ...state,
                isPlaying: !state.isPlaying
            }
    }
    return state;
};

const store = createStore(numberCounterApp);

class NumberCounter extends Component {
    render() {
        return <Provider store={store}>
            <NumberCounterContainer />
        </Provider>
    }
}

const TOGGLE_STATE = 'TOGGLE_STATE';

const toggleState = () => {
    return {
        type: TOGGLE_STATE,
    }
};


///**
// * Container component.
// */
//class NumberCounterContainer extends Component {
//    render() {
//        const {isPlaying, value, onPress} = this.props;
//        return <NumberCounterUI
//            isPlaying={isPlaying}
//            value={value}
//            onPress={onPress}
//        />;
//    }
//
//    toggleState() {
//        if (this.state.isPlaying) {
//            this.pause();
//        } else {
//            this.play();
//        }
//    }
//
//    pause() {
//        clearInterval(this.state.intervalId);
//        this.setState({
//            isPlaying: false,
//            intervalId: null,
//        });
//    }
//
//    play() {
//        const intervalId = setInterval(
//            () => this.countUp(),
//            1000);
//        this.setState({
//            isPlaying: true,
//            intervalId: intervalId,
//        })
//    }
//
//    countUp() {
//        const newValue = this.state.value + 1;
//        tts.speak({
//            text: '' + newValue,
//        });
//
//        this.setState({
//            value: newValue,
//        });
//    }
//}

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
